package com.hotelaventura.service;

import com.hotelaventura.dto.IncidenciaDTO;

import java.util.List;

public interface IncidenciaService {
    IncidenciaDTO reportar(IncidenciaDTO dto, String usuario);
    IncidenciaDTO asignarTecnico(Long id, String tecnico);
    IncidenciaDTO resolver(Long id, String solucion, String usuario);
    IncidenciaDTO obtenerPorId(Long id);
    List<IncidenciaDTO> listarTodas();
    List<IncidenciaDTO> listarPorHabitacion(Long habitacionId);
    List<IncidenciaDTO> listarPendientes();
}
