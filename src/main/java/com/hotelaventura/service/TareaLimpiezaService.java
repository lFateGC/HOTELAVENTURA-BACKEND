package com.hotelaventura.service;

import com.hotelaventura.dto.TareaLimpiezaDTO;

import java.util.List;

public interface TareaLimpiezaService {
    TareaLimpiezaDTO crear(TareaLimpiezaDTO dto, String usuario);
    TareaLimpiezaDTO iniciarTarea(Long id, String operario);
    TareaLimpiezaDTO completarTarea(Long id, String operario);
    List<TareaLimpiezaDTO> listarTodas();
    List<TareaLimpiezaDTO> listarPendientes();
    List<TareaLimpiezaDTO> listarPorHabitacion(Long habitacionId);
}
