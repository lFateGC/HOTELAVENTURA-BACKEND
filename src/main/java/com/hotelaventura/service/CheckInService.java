package com.hotelaventura.service;

import com.hotelaventura.dto.CheckInRequestDTO;
import com.hotelaventura.dto.CheckOutRequestDTO;
import com.hotelaventura.entity.Estancia;

import java.util.List;

public interface CheckInService {
    Estancia realizarCheckIn(CheckInRequestDTO request, String usuario);
    Estancia realizarCheckOut(CheckOutRequestDTO request, String usuario);
    Estancia obtenerPorId(Long id);
    List<Estancia> listarEstanciasActivas();
    List<Estancia> listarTodas();
}
