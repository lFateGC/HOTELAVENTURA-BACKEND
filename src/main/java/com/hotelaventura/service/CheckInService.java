package com.hotelaventura.modulo_habitaciones_reservas.service;

import com.hotelaventura.modulo_habitaciones_reservas.dto.CheckInRequestDTO;
import com.hotelaventura.modulo_habitaciones_reservas.dto.CheckOutRequestDTO;
import com.hotelaventura.modulo_habitaciones_reservas.entity.Estancia;

import java.util.List;

public interface CheckInService {
    Estancia realizarCheckIn(CheckInRequestDTO request, String usuario);
    Estancia realizarCheckOut(CheckOutRequestDTO request, String usuario);
    Estancia obtenerPorId(Long id);
    List<Estancia> listarEstanciasActivas();
    List<Estancia> listarTodas();
}
