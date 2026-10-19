package com.hotelaventura.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_huespedes_mincetur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroHuesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estancia_id", nullable = false)
    private Long estanciaId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "huesped_id", nullable = false)
    private Huesped huesped;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_viaje", nullable = false, length = 30)
    @Builder.Default
    private MotivoViaje motivoViaje = MotivoViaje.TURISMO;

    @Builder.Default
    @Column(name = "es_titular", nullable = false)
    private Boolean esTitular = true;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(length = 250)
    private String observaciones;
}
