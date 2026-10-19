package com.hotelaventura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionDTO {
    private Long id;

    @NotBlank(message = "El numero de habitacion es obligatorio")
    private String numero;

    @NotNull(message = "El tipo de habitacion es obligatorio")
    private Long tipoHabitacionId;
    private String tipoHabitacionNombre;

    @NotNull(message = "El piso es obligatorio")
    private Long pisoId;
    private Integer pisoNumero;

    private String estadoOperativo;
    private String estadoLimpieza;
    private BigDecimal tarifaPersonalizada;
    private BigDecimal precioFinal;
    private Boolean activa;
    private String observaciones;
}
