package com.hotelaventura.service.impl;

import com.hotelaventura.dto.AuthResponse;
import com.hotelaventura.dto.LoginRequest;
import com.hotelaventura.entity.Usuario;
import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.repository.UsuarioRepository;
import com.hotelaventura.security.JwtUtil;
import com.hotelaventura.service.AuditoriaService;
import com.hotelaventura.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           AuditoriaService auditoriaService,
                           JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ip) {
        // Se puede iniciar sesion con username o con correo
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .or(() -> usuarioRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new BadRequestException("Usuario o contrasena incorrectos"));

        // Validamos si la cuenta fue desactivada
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("Cuenta desactivada. Comunicarse con administracion.");
        }

        // Verificamos hash de contrasena con BCrypt
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadRequestException("Usuario o contrasena incorrectos");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        Set<String> rolesNombres = usuario.getRoles().stream()
                .map(r -> r.getNombre().name())
                .collect(Collectors.toSet());

        // Generamos el token firmado con roles incluidos
        String token = jwtUtil.generarToken(usuario.getUsername(), rolesNombres);

        // Registro de auditoria
        auditoriaService.registrarAccion(
                usuario.getUsername(),
                "LOGIN",
                "Usuario",
                usuario.getId(),
                "Ingreso al sistema: " + usuario.getUsername(),
                ip
        );

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .roles(rolesNombres)
                .build();
    }
}
