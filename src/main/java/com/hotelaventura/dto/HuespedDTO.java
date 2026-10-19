package com.hotelaventura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HuespedDTO {
    private Long id;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El numero de documento es obligatorio")
    @Size(min = 6, max = 20, message = "El numero de documento debe tener entre 6 y 20 caracteres")
    private String numeroDocumento;

    private LocalDate fechaNacimiento;
    private String sexo;
    private String nacionalidad;
    private String residencia;
    private String telefono;
    private String email;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private String preferencias;
}
