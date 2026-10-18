package com.hotelaventura.modulo_habitaciones_reservas.service;

import com.hotelaventura.modulo_habitaciones_reservas.dto.HabitacionDTO;
import com.hotelaventura.modulo_habitaciones_reservas.entity.EstadoLimpieza;
import com.hotelaventura.modulo_habitaciones_reservas.entity.EstadoOperativo;

import java.util.List;

public interface HabitacionService {
    HabitacionDTO crear(HabitacionDTO dto);
    HabitacionDTO actualizar(Long id, HabitacionDTO dto);
    HabitacionDTO obtenerPorId(Long id);
    List<HabitacionDTO> listarTodas();
    List<HabitacionDTO> listarParaRack();
    List<HabitacionDTO> listarPorPiso(Long pisoId);
    HabitacionDTO cambiarEstadoOperativo(Long id, EstadoOperativo estadoOperativo, String usuario);
    HabitacionDTO cambiarEstadoLimpieza(Long id, EstadoLimpieza estadoLimpieza, String usuario);
}
