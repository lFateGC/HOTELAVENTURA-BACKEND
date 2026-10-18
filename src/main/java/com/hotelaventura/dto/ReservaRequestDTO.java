package com.hotelaventura.modulo_habitaciones_reservas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotBlank(message = "El nombre del titular es obligatorio")
    private String nombreTitular;

    @NotBlank(message = "El documento del titular es obligatorio")
    private String documentoTitular;

    private String telefonoTitular;
    private String emailTitular;

    @NotNull(message = "El tipo de habitacion es obligatorio")
    private Long tipoHabitacionId;

    private Long habitacionAsignadaId; // Opcional

    @NotNull(message = "La fecha de llegada es obligatoria")
    @FutureOrPresent(message = "La fecha de llegada no puede ser anterior a hoy")
    private LocalDate fechaLlegada;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate fechaSalida;

    @Min(value = 1, message = "Debe haber al menos 1 adulto")
    @Builder.Default
    private Integer adultos = 1;

    @Min(value = 0, message = "El numero de ninos no puede ser negativo")
    @Builder.Default
    private Integer ninos = 0;

    private BigDecimal adelantoDeposito;
}
