package com.hotelaventura.modulo_pos_caja_inventario.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class TurnoCajaDTO {
    private Long id;
    private String cajero;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;

    @NotNull(message = "El monto inicial de caja es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto inicial no puede ser negativo")
    private BigDecimal montoInicial;

    private BigDecimal totalVentasEfectivo;
    private BigDecimal totalVentasDigital;
    private BigDecimal montoFinalContado;
    private BigDecimal diferencia;
    private String estado;
    private String observaciones;
}
