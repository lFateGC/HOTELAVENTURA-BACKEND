package com.hotelaventura.service;

import com.hotelaventura.dto.HuespedDTO;
import com.hotelaventura.dto.RegistroHuespedDTO;

import java.util.List;

public interface HuespedService {
    HuespedDTO registrar(HuespedDTO dto);
    HuespedDTO actualizar(Long id, HuespedDTO dto);
    HuespedDTO obtenerPorId(Long id);
    HuespedDTO obtenerPorDocumento(String numeroDocumento);
    List<HuespedDTO> listarTodos();
    List<HuespedDTO> buscarPorNombre(String termino);
    RegistroHuespedDTO registrarEnEstancia(RegistroHuespedDTO dto);
    List<RegistroHuespedDTO> listarPorEstancia(Long estanciaId);
}
