package com.hotelaventura.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnos_caja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String cajero; // Username de quien abre caja

    @Column(name = "fecha_apertura", nullable = false)
    @Builder.Default
    private LocalDateTime fechaApertura = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "total_ventas_efectivo", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalVentasEfectivo = BigDecimal.ZERO;

    @Column(name = "total_ventas_digital", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalVentasDigital = BigDecimal.ZERO;

    @Column(name = "monto_final_contado", precision = 12, scale = 2)
    private BigDecimal montoFinalContado;

    @Column(precision = 12, scale = 2)
    private BigDecimal diferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoCaja estado = EstadoCaja.ABIERTA;

    @Column(length = 250)
    private String observaciones;
}
