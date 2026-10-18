package com.hotelaventura.service;

import com.hotelaventura.dto.VentaPOSDTO;
import com.hotelaventura.entity.VentaPOS;

import java.util.List;

public interface VentaPOSService {
    VentaPOSDTO procesarVenta(VentaPOSDTO dto, String usuario);
    VentaPOSDTO obtenerPorId(Long id);
    List<VentaPOSDTO> listarTodas();
}
