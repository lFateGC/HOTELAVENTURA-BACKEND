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
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargoDTO {
    private Long id;

    @NotNull(message = "El ID del folio es obligatorio")
    private Long folioId;

    @NotBlank(message = "El concepto del cargo es obligatorio")
    private String concepto;

    @Builder.Default
    private String categoria = "CONSUMO_POS";

    @Min(value = 1, message = "La cantidad minima es 1")
    @Builder.Default
    private Integer cantidad = 1;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser positivo")
    private BigDecimal precioUnitario;

    private BigDecimal montoTotal;
    private LocalDateTime fecha;
    private Boolean anulado;
    private String motivoAnulacion;
}
