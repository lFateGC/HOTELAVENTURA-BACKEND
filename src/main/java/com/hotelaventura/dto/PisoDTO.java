package com.hotelaventura.modulo_habitaciones_reservas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PisoDTO {
    private Long id;

    @NotNull(message = "El numero de piso es obligatorio")
    private Integer numero;

    @NotBlank(message = "El nombre del piso es obligatorio")
    private String nombre;
}
