package com.hotelaventura.service;

import com.hotelaventura.dto.ReservaRequestDTO;
import com.hotelaventura.dto.ReservaResponseDTO;

import java.util.List;

public interface ReservaService {
    ReservaResponseDTO crear(ReservaRequestDTO request, String usuario);
    ReservaResponseDTO obtenerPorId(Long id);
    ReservaResponseDTO obtenerPorCodigo(String codigo);
    List<ReservaResponseDTO> listarTodas();
    ReservaResponseDTO cancelar(Long id, String motivo, String usuario);
    ReservaResponseDTO confirmar(Long id, String usuario);
}
