package com.hotelaventura.modulo_seguridad.service;

import com.hotelaventura.modulo_seguridad.dto.AuditoriaDTO;

import java.util.List;

public interface AuditoriaService {
    void registrarAccion(String usuario, String accion, String entidad, Long idEntidad, String detalle, String ip);
    List<AuditoriaDTO> listarTodo();
    List<AuditoriaDTO> listarPorUsuario(String usuario);
    List<AuditoriaDTO> listarPorEntidad(String entidad);
}
