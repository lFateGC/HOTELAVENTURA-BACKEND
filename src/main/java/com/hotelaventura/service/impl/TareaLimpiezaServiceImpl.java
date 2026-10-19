package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.entity.EstadoLimpieza;
import com.hotelaventura.entity.Habitacion;
import com.hotelaventura.repository.HabitacionRepository;
import com.hotelaventura.dto.TareaLimpiezaDTO;
import com.hotelaventura.entity.EstadoLimpiezaTarea;
import com.hotelaventura.entity.TareaLimpieza;
import com.hotelaventura.entity.TipoLimpieza;
import com.hotelaventura.repository.TareaLimpiezaRepository;
import com.hotelaventura.service.TareaLimpiezaService;
import com.hotelaventura.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TareaLimpiezaServiceImpl implements TareaLimpiezaService {

    private final TareaLimpiezaRepository tareaLimpiezaRepository;
    private final HabitacionRepository habitacionRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public TareaLimpiezaDTO crear(TareaLimpiezaDTO dto, String usuario) {
        Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada"));

        TipoLimpieza tipo;
        try {
            tipo = TipoLimpieza.valueOf(dto.getTipo().toUpperCase());
        } catch (Exception e) {
            tipo = TipoLimpieza.SALIDA;
        }

        TareaLimpieza tarea = TareaLimpieza.builder()
                .habitacion(habitacion)
                .tipo(tipo)
                .estado(EstadoLimpiezaTarea.PENDIENTE)
                .asignadoA(dto.getAsignadoA())
                .fechaHoraCreacion(LocalDateTime.now())
                .notas(dto.getNotas())
                .build();

        TareaLimpieza guardada = tareaLimpiezaRepository.save(tarea);

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "HOUSEKEEPING",
                "CREAR_TAREA_LIMPIEZA",
                "TareaLimpieza",
                guardada.getId(),
                "Tarea de limpieza (" + tipo + ") creada para habitacion " + habitacion.getNumero(),
                "127.0.0.1"
        );

        return convertirADTO(guardada);
    }

    @Override
    @Transactional
    public TareaLimpiezaDTO iniciarTarea(Long id, String operario) {
        TareaLimpieza tarea = tareaLimpiezaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + id));

        tarea.setEstado(EstadoLimpiezaTarea.EN_PROCESO);
        tarea.setFechaHoraInicio(LocalDateTime.now());
        if (operario != null && !operario.isBlank()) {
            tarea.setAsignadoA(operario);
        }

        return convertirADTO(tareaLimpiezaRepository.save(tarea));
    }

    @Override
    @Transactional
    public TareaLimpiezaDTO completarTarea(Long id, String operario) {
        TareaLimpieza tarea = tareaLimpiezaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + id));

        tarea.setEstado(EstadoLimpiezaTarea.COMPLETADA);
        tarea.setFechaHoraFin(LocalDateTime.now());
        TareaLimpieza guardada = tareaLimpiezaRepository.save(tarea);

        // Actualizar habitacion a LIMPIA
        Habitacion habitacion = tarea.getHabitacion();
        habitacion.setEstadoLimpieza(EstadoLimpieza.LIMPIA);
        habitacionRepository.save(habitacion);

        auditoriaService.registrarAccion(
                operario != null ? operario : "HOUSEKEEPING",
                "COMPLETAR_LIMPIEZA",
                "Habitacion",
                habitacion.getId(),
                "Habitacion " + habitacion.getNumero() + " marcada como LIMPIA tras completar tarea #" + tarea.getId(),
                "127.0.0.1"
        );

        return convertirADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaLimpiezaDTO> listarTodas() {
        return tareaLimpiezaRepository.findAllByOrderByFechaHoraCreacionDesc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaLimpiezaDTO> listarPendientes() {
        return tareaLimpiezaRepository.findByEstado(EstadoLimpiezaTarea.PENDIENTE).stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaLimpiezaDTO> listarPorHabitacion(Long habitacionId) {
        return tareaLimpiezaRepository.findByHabitacionId(habitacionId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    private TareaLimpiezaDTO convertirADTO(TareaLimpieza t) {
        return TareaLimpiezaDTO.builder()
                .id(t.getId())
                .habitacionId(t.getHabitacion().getId())
                .habitacionNumero(t.getHabitacion().getNumero())
                .tipo(t.getTipo().name())
                .estado(t.getEstado().name())
                .asignadoA(t.getAsignadoA())
                .fechaHoraCreacion(t.getFechaHoraCreacion())
                .fechaHoraInicio(t.getFechaHoraInicio())
                .fechaHoraFin(t.getFechaHoraFin())
                .notas(t.getNotas())
                .build();
    }
}
