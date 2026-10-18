package com.hotelaventura.modulo_habitaciones_reservas.dto;

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
public class CheckInRequestDTO {

    private Long reservaId; // Opcional si es walk-in

    @NotNull(message = "La habitacion es obligatoria")
    private Long habitacionId;

    @Builder.Default
    private String modalidad = "PERNOCTE"; // PERNOCTE o DAY_USE

    @NotNull(message = "La fecha y hora de check-out previsto es obligatoria")
    private LocalDateTime checkOutPrevisto;

    private BigDecimal tarifaAcordada; // Si es null, se usa la del tipo de habitacion
    private String observaciones;
}
