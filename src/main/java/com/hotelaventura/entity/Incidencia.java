package com.hotelaventura.entity;

import com.hotelaventura.entity.Habitacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias_averias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PrioridadIncidencia prioridad = PrioridadIncidencia.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoIncidencia estado = EstadoIncidencia.REPORTADA;

    @Column(name = "reportada_por", nullable = false, length = 80)
    private String reportadaPor;

    @Column(name = "asignada_a", length = 80)
    private String asignadaA; // Tecnico de mantenimiento

    @Column(name = "fecha_reporte", nullable = false)
    @Builder.Default
    private LocalDateTime fechaReporte = LocalDateTime.now();

    @Column(name = "fecha_solucion")
    private LocalDateTime fechaSolucion;

    @Column(name = "solucion_aplicada", length = 500)
    private String solucionAplicada;

    @Column(name = "foto_evidencia_url", length = 500)
    private String fotoEvidenciaUrl;
}
