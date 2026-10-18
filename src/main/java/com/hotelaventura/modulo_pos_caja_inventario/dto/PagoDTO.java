package com.hotelaventura.modulo_pos_caja_inventario.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class PagoDTO {
    private Long id;

    @NotNull(message = "El ID del folio es obligatorio")
    private Long folioId;

    @NotBlank(message = "El metodo de pago es obligatorio")
    private String metodo;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private String referencia;
    private Long turnoCajaId;
    private LocalDateTime fecha;
}
