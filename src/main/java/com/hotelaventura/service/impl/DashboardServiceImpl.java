package com.hotelaventura.service.impl;

import com.hotelaventura.entity.EstadoEstancia;
import com.hotelaventura.entity.EstadoLimpieza;
import com.hotelaventura.entity.EstadoOperativo;
import com.hotelaventura.entity.EstadoReserva;
import com.hotelaventura.repository.EstanciaRepository;
import com.hotelaventura.repository.HabitacionRepository;
import com.hotelaventura.repository.ReservaRepository;
import com.hotelaventura.dto.DashboardKpiDTO;
import com.hotelaventura.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final HabitacionRepository habitacionRepository;
    private final EstanciaRepository estanciaRepository;
    private final ReservaRepository reservaRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardKpiDTO obtenerKpis() {
        // Sacamos las cuentas basicas para mostrar en el panel de administracion
        long total = habitacionRepository.count();
        long disponibles = habitacionRepository.countByEstadoOperativo(EstadoOperativo.DISPONIBLE);
        long ocupadas = habitacionRepository.countByEstadoOperativo(EstadoOperativo.OCUPADA);
        long enMantenimiento = habitacionRepository.countByEstadoOperativo(EstadoOperativo.FUERA_DE_SERVICIO)
                + habitacionRepository.countByEstadoOperativo(EstadoOperativo.BLOQUEADA);
        long enLimpieza = habitacionRepository.countByEstadoLimpieza(EstadoLimpieza.SUCIA);

        // Porcentaje simple de ocupacion actual
        double porcentajeOcupacion = total > 0 ? ((double) ocupadas / total) * 100.0 : 0.0;

        long estanciasActivas = estanciaRepository.countByEstado(EstadoEstancia.ACTIVA);
        long reservasPendientes = reservaRepository.countByEstado(EstadoReserva.PENDIENTE);
        long reservasConfirmadas = reservaRepository.countByEstado(EstadoReserva.CONFIRMADA);

        // Resumen de cuartos ocupados por piso
        Map<String, Long> ocupacionPorPiso = new HashMap<>();
        habitacionRepository.findAll().forEach(h -> {
            String pisoKey = "Piso " + (h.getPiso() != null ? h.getPiso().getNumero() : "General");
            if (h.getEstadoOperativo() == EstadoOperativo.OCUPADA) {
                ocupacionPorPiso.put(pisoKey, ocupacionPorPiso.getOrDefault(pisoKey, 0L) + 1);
            }
        });

        return DashboardKpiDTO.builder()
                .totalHabitaciones(total)
                .habitacionesDisponibles(disponibles)
                .habitacionesOcupadas(ocupadas)
                .habitacionesEnLimpieza(enLimpieza)
                .habitacionesEnMantenimiento(enMantenimiento)
                .porcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0)
                .estanciasActivas(estanciasActivas)
                .reservasPendientes(reservasPendientes)
                .reservasConfirmadas(reservasConfirmadas)
                .ingresosAproximadosMes(BigDecimal.valueOf(ocupadas * 120.0)) // Proyeccion referencial
                .ocupacionPorPiso(ocupacionPorPiso)
                .build();
    }
}
