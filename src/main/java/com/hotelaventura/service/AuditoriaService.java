package com.hotelaventura.service;

import com.hotelaventura.dto.AuditoriaDTO;

import java.util.List;

public interface AuditoriaService {
    void registrarAccion(String usuario, String accion, String entidad, Long entidadId, String detalle, String ip);
    List<AuditoriaDTO> listarTodas();
    List<AuditoriaDTO> listarPorUsuario(String usuario);
    List<AuditoriaDTO> listarPorAccion(String accion);
}
