package com.hotelaventura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKpiDTO {
    private long totalHabitaciones;
    private long habitacionesDisponibles;
    private long habitacionesOcupadas;
    private long habitacionesEnLimpieza;
    private long habitacionesEnMantenimiento;
    private double porcentajeOcupacion;
    private long estanciasActivas;
    private long reservasPendientes;
    private long reservasConfirmadas;
    private BigDecimal ingresosAproximadosMes;
    private Map<String, Long> ocupacionPorPiso;
}
