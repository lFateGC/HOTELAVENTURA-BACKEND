package com.hotelaventura.modulo_pos_caja_inventario.service;

import com.hotelaventura.modulo_pos_caja_inventario.dto.CargoDTO;
import com.hotelaventura.modulo_pos_caja_inventario.dto.PagoDTO;
import com.hotelaventura.modulo_pos_caja_inventario.entity.Cargo;
import com.hotelaventura.modulo_pos_caja_inventario.entity.Folio;
import com.hotelaventura.modulo_pos_caja_inventario.entity.Pago;

import java.util.List;

public interface FolioService {
    Folio crearParaEstancia(Long estanciaId);
    Folio obtenerPorEstanciaId(Long estanciaId);
    Folio obtenerPorId(Long id);
    Cargo agregarCargo(CargoDTO dto, String usuario);
    Pago registrarPago(PagoDTO dto, String usuario);
    List<Cargo> listarCargosPorFolio(Long folioId);
    List<Pago> listarPagosPorFolio(Long folioId);
    Folio cerrarFolio(Long folioId, String usuario);
}
