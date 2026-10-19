package com.hotelaventura.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentTypeOptions(Customizer.withDefaults())
                )
                // Restricciones de acceso por roles (RBAC) para el sistema Hotel Aventura
                .authorizeHttpRequests(auth -> auth
                        // 1. Peticiones CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Rutas Públicas (Autenticación y recursos)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/storage/**").permitAll()
                        .requestMatchers("/api/portal/**").permitAll()

                        // 3. Módulo Seguridad, Administración y Auditoría (Solo ADMIN)
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/auditoria/**").hasRole("ADMIN")

                        // 4. Módulo Dashboard y Métricas Gerenciales
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "RECEPCIONISTA")

                        // 5. Módulo Habitaciones, Tipos y Pisos
                        .requestMatchers(HttpMethod.GET, "/api/habitaciones/**", "/api/tipos-habitacion/**")
                                .hasAnyRole("ADMIN", "RECEPCIONISTA", "HOUSEKEEPING", "MANTENIMIENTO")
                        .requestMatchers("/api/habitaciones/**", "/api/tipos-habitacion/**")
                                .hasAnyRole("ADMIN", "RECEPCIONISTA")

                        // 6. Módulo Reservas, Check-In, Estancias, Folios y Huéspedes
                        .requestMatchers("/api/reservas/**", "/api/estancias/**", "/api/checkin/**", "/api/huespedes/**")
                                .hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers("/api/folios/**")
                                .hasAnyRole("ADMIN", "RECEPCIONISTA", "CAJERO")

                        // 7. Módulo Caja, Punto de Venta (POS) e Inventario
                        .requestMatchers("/api/caja/**", "/api/cajas/**", "/api/pos/**")
                                .hasAnyRole("ADMIN", "CAJERO", "RECEPCIONISTA")
                        .requestMatchers(HttpMethod.GET, "/api/productos/**")
                                .hasAnyRole("ADMIN", "CAJERO", "RECEPCIONISTA")
                        .requestMatchers("/api/productos/**")
                                .hasRole("ADMIN")

                        // 8. Módulo Limpieza y Mantenimiento / Averías
                        .requestMatchers("/api/limpieza/**")
                                .hasAnyRole("ADMIN", "HOUSEKEEPING", "RECEPCIONISTA")
                        .requestMatchers("/api/incidencias/**")
                                .hasAnyRole("ADMIN", "MANTENIMIENTO", "HOUSEKEEPING", "RECEPCIONISTA")

                        // 9. Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
