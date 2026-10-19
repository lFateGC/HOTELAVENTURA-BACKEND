package com.hotelaventura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaDTO {
    private Long id;

    @NotNull(message = "El ID de la habitacion es obligatorio")
    private Long habitacionId;
    private String habitacionNumero;

    @NotBlank(message = "El titulo de la incidencia es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripcion de la averia es obligatoria")
    private String descripcion;

    @Builder.Default
    private String prioridad = "MEDIA"; // BAJA, MEDIA, ALTA, URGENTE

    private String estado; // REPORTADA, EN_REPARACION, RESUELTA

    @NotBlank(message = "El nombre de quien reporta es obligatorio")
    private String reportadaPor;

    private String asignadaA;
    private LocalDateTime fechaReporte;
    private LocalDateTime fechaSolucion;
    private String solucionAplicada;
    private String fotoEvidenciaUrl;
}
