package com.hotelaventura.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Registro inmutable de eventos sensibles realizados en el sistema
@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quién realizó la acción (username o SYSTEM)
    @Column(nullable = false, length = 50)
    private String usuario;

    // Tipo de acción: LOGIN, CREAR_USUARIO, CHECK_IN, CHECK_OUT, VENTA_POS, etc.
    @Column(nullable = false, length = 50)
    private String accion;

    // Módulo o tabla afectada: Usuario, Habitacion, Reserva, etc.
    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    // Detalle descriptivo de lo ocurrido
    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Column(length = 45)
    private String ip;

    @Builder.Default
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();
}
