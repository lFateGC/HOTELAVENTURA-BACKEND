package com.hotelaventura.modulo_habitaciones_reservas.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.modulo_habitaciones_reservas.dto.HabitacionDTO;
import com.hotelaventura.modulo_habitaciones_reservas.entity.*;
import com.hotelaventura.modulo_habitaciones_reservas.repository.HabitacionRepository;
import com.hotelaventura.modulo_habitaciones_reservas.repository.PisoRepository;
import com.hotelaventura.modulo_habitaciones_reservas.repository.TipoHabitacionRepository;
import com.hotelaventura.modulo_habitaciones_reservas.service.HabitacionService;
import com.hotelaventura.modulo_seguridad.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final PisoRepository pisoRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public HabitacionDTO crear(HabitacionDTO dto) {
        if (habitacionRepository.existsByNumero(dto.getNumero())) {
            throw new BadRequestException("Ya existe una habitacion registrada con el numero: " + dto.getNumero());
        }

        TipoHabitacion tipo = tipoHabitacionRepository.findById(dto.getTipoHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de habitacion no encontrado"));

        Piso piso = pisoRepository.findById(dto.getPisoId())
                .orElseThrow(() -> new ResourceNotFoundException("Piso no encontrado"));

        Habitacion habitacion = Habitacion.builder()
                .numero(dto.getNumero())
                .tipoHabitacion(tipo)
                .piso(piso)
                .estadoOperativo(EstadoOperativo.DISPONIBLE)
                .estadoLimpieza(EstadoLimpieza.LIMPIA)
                .tarifaPersonalizada(dto.getTarifaPersonalizada())
                .activa(true)
                .observaciones(dto.getObservaciones())
                .build();

        Habitacion guardada = habitacionRepository.save(habitacion);

        auditoriaService.registrarAccion(
                "ADMIN",
                "CREAR_HABITACION",
                "Habitacion",
                guardada.getId(),
                "Habitacion creada: " + guardada.getNumero() + " (" + tipo.getNombre() + ")",
                "127.0.0.1"
        );

        return convertirADTO(guardada);
    }

    @Override
    @Transactional
    public HabitacionDTO actualizar(Long id, HabitacionDTO dto) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada con ID: " + id));

        if (!habitacion.getNumero().equalsIgnoreCase(dto.getNumero()) && habitacionRepository.existsByNumero(dto.getNumero())) {
            throw new BadRequestException("El numero de habitacion '" + dto.getNumero() + "' ya esta en uso");
        }

        TipoHabitacion tipo = tipoHabitacionRepository.findById(dto.getTipoHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de habitacion no encontrado"));

        Piso piso = pisoRepository.findById(dto.getPisoId())
                .orElseThrow(() -> new ResourceNotFoundException("Piso no encontrado"));

        habitacion.setNumero(dto.getNumero());
        habitacion.setTipoHabitacion(tipo);
        habitacion.setPiso(piso);
        habitacion.setTarifaPersonalizada(dto.getTarifaPersonalizada());
        habitacion.setObservaciones(dto.getObservaciones());

        if (dto.getActiva() != null) {
            habitacion.setActiva(dto.getActiva());
        }

        return convertirADTO(habitacionRepository.save(habitacion));
    }

    @Override
    @Transactional(readOnly = true)
    public HabitacionDTO obtenerPorId(Long id) {
        return habitacionRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> listarTodas() {
        return habitacionRepository.findAllByOrderByNumeroAsc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> listarParaRack() {
        return listarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitacionDTO> listarPorPiso(Long pisoId) {
        return habitacionRepository.findByPisoId(pisoId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional
    public HabitacionDTO cambiarEstadoOperativo(Long id, EstadoOperativo estadoOperativo, String usuario) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada con ID: " + id));

        habitacion.setEstadoOperativo(estadoOperativo);
        Habitacion actualizada = habitacionRepository.save(habitacion);

        auditoriaService.registrarAccion(
                usuario,
                "CAMBIO_ESTADO_OPERATIVO",
                "Habitacion",
                actualizada.getId(),
                "Habitacion " + actualizada.getNumero() + " cambio a estado: " + estadoOperativo.name(),
                "127.0.0.1"
        );

        return convertirADTO(actualizada);
    }

    @Override
    @Transactional
    public HabitacionDTO cambiarEstadoLimpieza(Long id, EstadoLimpieza estadoLimpieza, String usuario) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada con ID: " + id));

        habitacion.setEstadoLimpieza(estadoLimpieza);
        Habitacion actualizada = habitacionRepository.save(habitacion);

        auditoriaService.registrarAccion(
                usuario,
                "CAMBIO_ESTADO_LIMPIEZA",
                "Habitacion",
                actualizada.getId(),
                "Habitacion " + actualizada.getNumero() + " cambio a limpieza: " + estadoLimpieza.name(),
                "127.0.0.1"
        );

        return convertirADTO(actualizada);
    }

    private HabitacionDTO convertirADTO(Habitacion h) {
        BigDecimal precioFinal = h.getTarifaPersonalizada() != null
                ? h.getTarifaPersonalizada()
                : (h.getTipoHabitacion() != null ? h.getTipoHabitacion().getPrecioBase() : BigDecimal.ZERO);

        return HabitacionDTO.builder()
                .id(h.getId())
                .numero(h.getNumero())
                .tipoHabitacionId(h.getTipoHabitacion() != null ? h.getTipoHabitacion().getId() : null)
                .tipoHabitacionNombre(h.getTipoHabitacion() != null ? h.getTipoHabitacion().getNombre() : null)
                .pisoId(h.getPiso() != null ? h.getPiso().getId() : null)
                .pisoNumero(h.getPiso() != null ? h.getPiso().getNumero() : null)
                .estadoOperativo(h.getEstadoOperativo().name())
                .estadoLimpieza(h.getEstadoLimpieza().name())
                .tarifaPersonalizada(h.getTarifaPersonalizada())
                .precioFinal(precioFinal)
                .activa(h.getActiva())
                .observaciones(h.getObservaciones())
                .build();
    }
}
