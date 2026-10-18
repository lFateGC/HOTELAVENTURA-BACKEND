package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.dto.CheckInRequestDTO;
import com.hotelaventura.dto.CheckOutRequestDTO;
import com.hotelaventura.entity.*;
import com.hotelaventura.repository.EstanciaRepository;
import com.hotelaventura.repository.HabitacionRepository;
import com.hotelaventura.repository.ReservaRepository;
import com.hotelaventura.service.CheckInService;
import com.hotelaventura.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final EstanciaRepository estanciaRepository;
    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public Estancia realizarCheckIn(CheckInRequestDTO request, String usuario) {
        Habitacion habitacion = habitacionRepository.findById(request.getHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada"));

        // Validamos que el cuarto este libre y en condiciones
        if (habitacion.getEstadoOperativo() == EstadoOperativo.OCUPADA) {
            throw new BadRequestException("La habitacion " + habitacion.getNumero() + " ya se encuentra ocupada");
        }
        if (habitacion.getEstadoOperativo() == EstadoOperativo.FUERA_DE_SERVICIO) {
            throw new BadRequestException("La habitacion " + habitacion.getNumero() + " esta fuera de servicio por mantenimiento");
        }

        Reserva reserva = null;
        if (request.getReservaId() != null) {
            reserva = reservaRepository.findById(request.getReservaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
            reserva.setEstado(EstadoReserva.EN_CASA);
            reserva.setHabitacionAsignada(habitacion);
            reservaRepository.save(reserva);
        }

        ModalidadEstancia modalidad;
        try {
            modalidad = ModalidadEstancia.valueOf(request.getModalidad().toUpperCase());
        } catch (Exception e) {
            modalidad = ModalidadEstancia.PERNOCTE;
        }

        // Si no mandan tarifa acordada, tomamos el precio del tipo de habitacion
        BigDecimal tarifa = request.getTarifaAcordada();
        if (tarifa == null) {
            tarifa = habitacion.getTarifaPersonalizada() != null
                    ? habitacion.getTarifaPersonalizada()
                    : habitacion.getTipoHabitacion().getPrecioBase();
        }

        String codigoEstancia = "EST-" + System.currentTimeMillis() % 1000000;

        Estancia estancia = Estancia.builder()
                .codigo(codigoEstancia)
                .reserva(reserva)
                .habitacion(habitacion)
                .modalidad(modalidad)
                .checkInReal(LocalDateTime.now())
                .checkOutPrevisto(request.getCheckOutPrevisto())
                .tarifaAcordada(tarifa)
                .estado(EstadoEstancia.ACTIVA)
                .observaciones(request.getObservaciones())
                .build();

        Estancia guardada = estanciaRepository.save(estancia);

        // Marcamos la habitacion como ocupada
        habitacion.setEstadoOperativo(EstadoOperativo.OCUPADA);
        habitacionRepository.save(habitacion);

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "RECEPCION",
                "CHECK_IN",
                "Estancia",
                guardada.getId(),
                "Check-in realizado en habitacion " + habitacion.getNumero() + " (Modalidad: " + modalidad + ")",
                "127.0.0.1"
        );

        return guardada;
    }

    @Override
    @Transactional
    public Estancia realizarCheckOut(CheckOutRequestDTO request, String usuario) {
        Estancia estancia = estanciaRepository.findById(request.getEstanciaId())
                .orElseThrow(() -> new ResourceNotFoundException("Estancia no encontrada"));

        if (estancia.getEstado() == EstadoEstancia.FINALIZADA) {
            throw new BadRequestException("Esta estancia ya fue finalizada anteriormente");
        }

        estancia.setEstado(EstadoEstancia.FINALIZADA);
        estancia.setCheckOutReal(LocalDateTime.now());
        if (request.getObservaciones() != null) {
            estancia.setObservaciones((estancia.getObservaciones() != null ? estancia.getObservaciones() + " | " : "") + request.getObservaciones());
        }

        Estancia guardada = estanciaRepository.save(estancia);

        // Al salir el huesped, la habitacion queda libre pero pasa a SUCIA para limpieza
        Habitacion habitacion = estancia.getHabitacion();
        habitacion.setEstadoOperativo(EstadoOperativo.DISPONIBLE);
        habitacion.setEstadoLimpieza(EstadoLimpieza.SUCIA);
        habitacionRepository.save(habitacion);

        if (estancia.getReserva() != null) {
            Reserva reserva = estancia.getReserva();
            reserva.setEstado(EstadoReserva.FINALIZADA);
            reservaRepository.save(reserva);
        }

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "RECEPCION",
                "CHECK_OUT",
                "Estancia",
                guardada.getId(),
                "Check-out procesado para habitacion " + habitacion.getNumero() + ". Pasa a estado SUCIA.",
                "127.0.0.1"
        );

        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Estancia obtenerPorId(Long id) {
        return estanciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estancia no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estancia> listarEstanciasActivas() {
        return estanciaRepository.findByEstado(EstadoEstancia.ACTIVA);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estancia> listarTodas() {
        return estanciaRepository.findAllByOrderByCheckInRealDesc();
    }
}
