package com.hotelaventura.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class TipoHabitacionDTO {
    private Long id;

    @NotBlank(message = "El nombre del tipo de habitacion es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La capacidad de adultos es obligatoria")
    @Min(value = 1, message = "Debe permitir al menos 1 adulto")
    private Integer capacidadAdultos;

    @NotNull(message = "La capacidad de ninos es obligatoria")
    @Min(value = 0, message = "La capacidad de ninos no puede ser negativa")
    private Integer capacidadNinos;

    @NotNull(message = "El precio base por noche es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precioBase;

    private BigDecimal precioDayUse;

    private String imagenUrl;
}
