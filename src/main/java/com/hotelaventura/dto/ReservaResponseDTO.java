package com.hotelaventura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private Long id;
    private String codigo;
    private String nombreTitular;
    private String documentoTitular;
    private String telefonoTitular;
    private String emailTitular;
    private Long tipoHabitacionId;
    private String tipoHabitacionNombre;
    private Long habitacionAsignadaId;
    private String habitacionNumero;
    private LocalDate fechaLlegada;
    private LocalDate fechaSalida;
    private Integer noches;
    private Integer adultos;
    private Integer ninos;
    private String estado;
    private BigDecimal totalEstimado;
    private BigDecimal adelantoDeposito;
    private String motivoCancelacion;
    private LocalDateTime fechaCreacion;
}
