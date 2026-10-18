package com.hotelaventura.modulo_habitaciones_reservas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "habitaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String numero; // "101", "202", etc.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "piso_id", nullable = false)
    private Piso piso;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_operativo", nullable = false, length = 30)
    @Builder.Default
    private EstadoOperativo estadoOperativo = EstadoOperativo.DISPONIBLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_limpieza", nullable = false, length = 30)
    @Builder.Default
    private EstadoLimpieza estadoLimpieza = EstadoLimpieza.LIMPIA;

    @Column(name = "tarifa_personalizada", precision = 12, scale = 2)
    private BigDecimal tarifaPersonalizada;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activa = true;

    @Column(length = 200)
    private String observaciones;
}
