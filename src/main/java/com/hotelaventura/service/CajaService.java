package com.hotelaventura.service;

import com.hotelaventura.dto.TurnoCajaDTO;
import com.hotelaventura.entity.TurnoCaja;

import java.math.BigDecimal;
import java.util.List;

public interface CajaService {
    TurnoCaja abrirTurno(TurnoCajaDTO dto, String cajero);
    TurnoCaja cerrarTurno(Long id, BigDecimal montoFinalContado, String observaciones, String cajero);
    TurnoCaja obtenerTurnoActual();
    List<TurnoCaja> listarTurnos();
}
