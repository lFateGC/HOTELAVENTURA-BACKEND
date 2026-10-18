package com.hotelaventura.service;

import com.hotelaventura.dto.CargoDTO;
import com.hotelaventura.dto.PagoDTO;
import com.hotelaventura.entity.Cargo;
import com.hotelaventura.entity.Folio;
import com.hotelaventura.entity.Pago;

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
