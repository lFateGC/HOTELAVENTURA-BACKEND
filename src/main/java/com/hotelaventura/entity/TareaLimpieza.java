package com.hotelaventura.entity;

import com.hotelaventura.entity.Habitacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tareas_limpieza")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaLimpieza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TipoLimpieza tipo = TipoLimpieza.SALIDA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoLimpiezaTarea estado = EstadoLimpiezaTarea.PENDIENTE;

    @Column(name = "asignado_a", length = 80)
    private String asignadoA; // Nombre del personal de housekeeping

    @Column(name = "fecha_hora_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaHoraCreacion = LocalDateTime.now();

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Column(length = 250)
    private String notas;
}
