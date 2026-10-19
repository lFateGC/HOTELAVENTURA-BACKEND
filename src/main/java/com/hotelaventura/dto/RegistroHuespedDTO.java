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
public class RegistroHuespedDTO {
    private Long id;

    @NotNull(message = "El ID de la estancia es obligatorio")
    private Long estanciaId;

    @NotNull(message = "El ID del huesped es obligatorio")
    private Long huespedId;
    private String huespedNombreCompleto;
    private String huespedDocumento;

    @Builder.Default
    private String motivoViaje = "TURISMO";

    @Builder.Default
    private Boolean esTitular = true;

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private String observaciones;
}
