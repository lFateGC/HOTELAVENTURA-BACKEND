package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.dto.ReservaRequestDTO;
import com.hotelaventura.dto.ReservaResponseDTO;
import com.hotelaventura.entity.*;
import com.hotelaventura.repository.HabitacionRepository;
import com.hotelaventura.repository.ReservaRepository;
import com.hotelaventura.repository.TipoHabitacionRepository;
import com.hotelaventura.service.ReservaService;
import com.hotelaventura.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public ReservaResponseDTO crear(ReservaRequestDTO request, String usuario) {
        if (!request.getFechaSalida().isAfter(request.getFechaLlegada())) {
            throw new BadRequestException("La fecha de salida debe ser posterior a la fecha de llegada");
        }

        TipoHabitacion tipo = tipoHabitacionRepository.findById(request.getTipoHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de habitacion no encontrado"));

        long noches = ChronoUnit.DAYS.between(request.getFechaLlegada(), request.getFechaSalida());
        if (noches <= 0) {
            noches = 1;
        }

        Habitacion habitacionAsignada = null;
        if (request.getHabitacionAsignadaId() != null) {
            habitacionAsignada = habitacionRepository.findById(request.getHabitacionAsignadaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada"));

            if (habitacionAsignada.getEstadoOperativo() == EstadoOperativo.FUERA_DE_SERVICIO) {
                throw new BadRequestException("La habitacion seleccionada se encuentra fuera de servicio");
            }
        }

        BigDecimal precioNoche = habitacionAsignada != null && habitacionAsignada.getTarifaPersonalizada() != null
                ? habitacionAsignada.getTarifaPersonalizada()
                : tipo.getPrecioBase();

        BigDecimal totalEstimado = precioNoche.multiply(BigDecimal.valueOf(noches));

        String codigo = "RES-" + System.currentTimeMillis() % 1000000;

        Reserva reserva = Reserva.builder()
                .codigo(codigo)
                .nombreTitular(request.getNombreTitular())
                .documentoTitular(request.getDocumentoTitular())
                .telefonoTitular(request.getTelefonoTitular())
                .emailTitular(request.getEmailTitular())
                .tipoHabitacion(tipo)
                .habitacionAsignada(habitacionAsignada)
                .fechaLlegada(request.getFechaLlegada())
                .fechaSalida(request.getFechaSalida())
                .adultos(request.getAdultos() != null ? request.getAdultos() : 1)
                .ninos(request.getNinos() != null ? request.getNinos() : 0)
                .estado(EstadoReserva.CONFIRMADA)
                .totalEstimado(totalEstimado)
                .adelantoDeposito(request.getAdelantoDeposito() != null ? request.getAdelantoDeposito() : BigDecimal.ZERO)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Reserva guardada = reservaRepository.save(reserva);

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "RECEPCION",
                "CREAR_RESERVA",
                "Reserva",
                guardada.getId(),
                "Reserva creada: " + guardada.getCodigo() + " para " + guardada.getNombreTitular(),
                "127.0.0.1"
        );

        return convertirADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorCodigo(String codigo) {
        return reservaRepository.findByCodigo(codigo)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con codigo: " + codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional
    public ReservaResponseDTO cancelar(Long id, String motivo, String usuario) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        if (reserva.getEstado() == EstadoReserva.EN_CASA || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new BadRequestException("No se puede cancelar una reserva que ya ha sido ingresada o finalizada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setMotivoCancelacion(motivo != null ? motivo : "Cancelada por recepcion/cliente");

        Reserva actualizada = reservaRepository.save(reserva);

        auditoriaService.registrarAccion(
                usuario,
                "CANCELAR_RESERVA",
                "Reserva",
                actualizada.getId(),
                "Reserva " + actualizada.getCodigo() + " cancelada. Motivo: " + motivo,
                "127.0.0.1"
        );

        return convertirADTO(actualizada);
    }

    @Override
    @Transactional
    public ReservaResponseDTO confirmar(Long id, String usuario) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return convertirADTO(reservaRepository.save(reserva));
    }

    private ReservaResponseDTO convertirADTO(Reserva r) {
        long noches = ChronoUnit.DAYS.between(r.getFechaLlegada(), r.getFechaSalida());
        if (noches <= 0) noches = 1;

        return ReservaResponseDTO.builder()
                .id(r.getId())
                .codigo(r.getCodigo())
                .nombreTitular(r.getNombreTitular())
                .documentoTitular(r.getDocumentoTitular())
                .telefonoTitular(r.getTelefonoTitular())
                .emailTitular(r.getEmailTitular())
                .tipoHabitacionId(r.getTipoHabitacion() != null ? r.getTipoHabitacion().getId() : null)
                .tipoHabitacionNombre(r.getTipoHabitacion() != null ? r.getTipoHabitacion().getNombre() : null)
                .habitacionAsignadaId(r.getHabitacionAsignada() != null ? r.getHabitacionAsignada().getId() : null)
                .habitacionNumero(r.getHabitacionAsignada() != null ? r.getHabitacionAsignada().getNumero() : null)
                .fechaLlegada(r.getFechaLlegada())
                .fechaSalida(r.getFechaSalida())
                .noches((int) noches)
                .adultos(r.getAdultos())
                .ninos(r.getNinos())
                .estado(r.getEstado().name())
                .totalEstimado(r.getTotalEstimado())
                .adelantoDeposito(r.getAdelantoDeposito())
                .motivoCancelacion(r.getMotivoCancelacion())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}
