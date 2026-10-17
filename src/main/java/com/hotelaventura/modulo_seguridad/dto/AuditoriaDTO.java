package com.hotelaventura.modulo_seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDTO {
    private Long id;
    private String usuario;
    private String accion;
    private String entidad;
    private Long idEntidad;
    private String detalle;
    private String ip;
    private LocalDateTime fechaHora;
}
