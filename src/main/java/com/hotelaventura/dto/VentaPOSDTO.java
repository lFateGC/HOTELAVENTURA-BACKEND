package com.hotelaventura.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaPOSDTO {
    private Long id;
    private String codigo;
    private String vendedor;
    private Long folioId; // Si es nulo, es venta directa de mostrador
    private String habitacionNumero;

    @NotNull(message = "El metodo de pago es obligatorio")
    private String metodoPago;

    private BigDecimal montoTotal;
    private LocalDateTime fecha;

    @NotEmpty(message = "La venta debe incluir al menos un producto")
    private List<ItemVentaDTO> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemVentaDTO {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;
        private String productoNombre;

        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;

        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
