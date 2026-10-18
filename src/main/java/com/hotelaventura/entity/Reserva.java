package com.hotelaventura.modulo_habitaciones_reservas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo; // "RES-2026-001"

    @Column(name = "nombre_titular", nullable = false, length = 120)
    private String nombreTitular;

    @Column(name = "documento_titular", nullable = false, length = 20)
    private String documentoTitular;

    @Column(name = "telefono_titular", length = 30)
    private String telefonoTitular;

    @Column(name = "email_titular", length = 100)
    private String emailTitular;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitacion_asignada_id")
    private Habitacion habitacionAsignada; // Se asigna al confirmar o al check-in

    @Column(name = "fecha_llegada", nullable = false)
    private LocalDate fechaLlegada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    @Builder.Default
    private Integer adultos = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer ninos = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.CONFIRMADA;

    @Column(name = "total_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalEstimado;

    @Column(name = "adelanto_deposito", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal adelantoDeposito = BigDecimal.ZERO;

    @Column(name = "motivo_cancelacion", length = 250)
    private String motivoCancelacion;

    @Builder.Default
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
