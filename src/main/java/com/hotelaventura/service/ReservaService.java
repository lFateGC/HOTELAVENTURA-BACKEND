package com.hotelaventura.service;

import com.hotelaventura.dto.ReservaRequestDTO;
import com.hotelaventura.dto.ReservaResponseDTO;
import com.hotelaventura.entity.EstadoReserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {
    ReservaResponseDTO crear(ReservaRequestDTO request, String usuario);
    ReservaResponseDTO obtenerPorId(Long id);
    ReservaResponseDTO obtenerPorCodigo(String codigo);
    List<ReservaResponseDTO> listarTodas();
    List<ReservaResponseDTO> listarLlegadasHoy(LocalDate fecha);
    List<ReservaResponseDTO> listarSalidasHoy(LocalDate fecha);
    List<ReservaResponseDTO> listarPorEstado(EstadoReserva estado);
    ReservaResponseDTO cancelar(Long id, String motivo, String usuario);
    ReservaResponseDTO confirmar(Long id, String usuario);
}
