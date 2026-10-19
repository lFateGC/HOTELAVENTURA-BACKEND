package com.hotelaventura.dto;

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
public class TareaLimpiezaDTO {
    private Long id;

    @NotNull(message = "El ID de la habitacion es obligatorio")
    private Long habitacionId;
    private String habitacionNumero;

    @Builder.Default
    private String tipo = "SALIDA"; // SALIDA, ESTANCIA, RETOQUE, PROFUNDA

    private String estado;
    private String asignadoA;
    private LocalDateTime fechaHoraCreacion;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String notas;
}
