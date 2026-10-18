package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.dto.TurnoCajaDTO;
import com.hotelaventura.entity.EstadoCaja;
import com.hotelaventura.entity.TurnoCaja;
import com.hotelaventura.repository.TurnoCajaRepository;
import com.hotelaventura.service.CajaService;
import com.hotelaventura.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final TurnoCajaRepository turnoCajaRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public TurnoCaja abrirTurno(TurnoCajaDTO dto, String cajero) {
        if (turnoCajaRepository.findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA).isPresent()) {
            throw new BadRequestException("Ya existe un turno de caja abierto actualmente. Debe cerrarlo antes de iniciar otro.");
        }

        TurnoCaja turno = TurnoCaja.builder()
                .cajero(cajero != null ? cajero : "CAJERO")
                .fechaApertura(LocalDateTime.now())
                .montoInicial(dto.getMontoInicial())
                .totalVentasEfectivo(BigDecimal.ZERO)
                .totalVentasDigital(BigDecimal.ZERO)
                .estado(EstadoCaja.ABIERTA)
                .observaciones(dto.getObservaciones())
                .build();

        TurnoCaja guardado = turnoCajaRepository.save(turno);

        auditoriaService.registrarAccion(
                cajero,
                "APERTURA_CAJA",
                "TurnoCaja",
                guardado.getId(),
                "Apertura de turno de caja iniciada con monto base: S/ " + dto.getMontoInicial(),
                "127.0.0.1"
        );

        return guardado;
    }

    @Override
    @Transactional
    public TurnoCaja cerrarTurno(Long id, BigDecimal montoFinalContado, String observaciones, String cajero) {
        TurnoCaja turno = turnoCajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno de caja no encontrado con ID: " + id));

        if (turno.getEstado() == EstadoCaja.CERRADA) {
            throw new BadRequestException("El turno de caja ya se encuentra cerrado.");
        }

        BigDecimal totalEsperado = turno.getMontoInicial().add(turno.getTotalVentasEfectivo());
        BigDecimal diferencia = montoFinalContado.subtract(totalEsperado);

        turno.setFechaCierre(LocalDateTime.now());
        turno.setMontoFinalContado(montoFinalContado);
        turno.setDiferencia(diferencia);
        turno.setEstado(EstadoCaja.CERRADA);
        if (observaciones != null) {
            turno.setObservaciones((turno.getObservaciones() != null ? turno.getObservaciones() + " | " : "") + observaciones);
        }

        TurnoCaja guardado = turnoCajaRepository.save(turno);

        auditoriaService.registrarAccion(
                cajero != null ? cajero : "CAJERO",
                "CIERRE_CAJA",
                "TurnoCaja",
                guardado.getId(),
                "Cierre de caja completado. Monto contado: S/ " + montoFinalContado + " (Diferencia: S/ " + diferencia + ")",
                "127.0.0.1"
        );

        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoCaja obtenerTurnoActual() {
        return turnoCajaRepository.findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA)
                .orElseThrow(() -> new ResourceNotFoundException("No hay ningun turno de caja abierto en este momento."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoCaja> listarTurnos() {
        return turnoCajaRepository.findAllByOrderByFechaAperturaDesc();
    }
}
