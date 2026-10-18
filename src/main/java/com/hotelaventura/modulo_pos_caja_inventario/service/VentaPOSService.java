package com.hotelaventura.modulo_pos_caja_inventario.service;

import com.hotelaventura.modulo_pos_caja_inventario.dto.VentaPOSDTO;
import com.hotelaventura.modulo_pos_caja_inventario.entity.VentaPOS;

import java.util.List;

public interface VentaPOSService {
    VentaPOSDTO procesarVenta(VentaPOSDTO dto, String usuario);
    VentaPOSDTO obtenerPorId(Long id);
    List<VentaPOSDTO> listarTodas();
}
