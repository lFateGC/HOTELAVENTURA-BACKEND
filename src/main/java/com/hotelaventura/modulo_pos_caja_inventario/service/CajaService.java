package com.hotelaventura.modulo_pos_caja_inventario.service;

import com.hotelaventura.modulo_pos_caja_inventario.dto.TurnoCajaDTO;
import com.hotelaventura.modulo_pos_caja_inventario.entity.TurnoCaja;

import java.math.BigDecimal;
import java.util.List;

public interface CajaService {
    TurnoCaja abrirTurno(TurnoCajaDTO dto, String cajero);
    TurnoCaja cerrarTurno(Long id, BigDecimal montoFinalContado, String observaciones, String cajero);
    TurnoCaja obtenerTurnoActual();
    List<TurnoCaja> listarTurnos();
}
