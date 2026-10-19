package com.hotelaventura.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estancias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo; // "EST-2026-001"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva; // Puede ser null si es walk-in directo

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ModalidadEstancia modalidad = ModalidadEstancia.PERNOCTE;

    @Column(name = "check_in_real", nullable = false)
    private LocalDateTime checkInReal;

    @Column(name = "check_out_previsto", nullable = false)
    private LocalDateTime checkOutPrevisto;

    @Column(name = "check_out_real")
    private LocalDateTime checkOutReal;

    @Column(name = "tarifa_acordada", nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifaAcordada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoEstancia estado = EstadoEstancia.ACTIVA;

    @Column(length = 300)
    private String observaciones;
}
