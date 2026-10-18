package com.hotelaventura.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequestDTO {

    @NotNull(message = "El ID de la estancia es obligatorio")
    private Long estanciaId;

    private String observaciones;
    private String usuarioResponsable;
}
