package com.hotelaventura.modulo_pos_caja_inventario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "folios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estancia_id", nullable = false, unique = true)
    private Long estanciaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoFolio estado = EstadoFolio.ABIERTO;

    @Column(name = "total_cargos", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalCargos = BigDecimal.ZERO;

    @Column(name = "total_abonos", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalAbonos = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "fecha_apertura", nullable = false)
    @Builder.Default
    private LocalDateTime fechaApertura = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
}
