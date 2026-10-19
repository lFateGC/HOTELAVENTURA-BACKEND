package com.hotelaventura.service.impl;

import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.entity.EstadoOperativo;
import com.hotelaventura.entity.Habitacion;
import com.hotelaventura.repository.HabitacionRepository;
import com.hotelaventura.dto.IncidenciaDTO;
import com.hotelaventura.entity.EstadoIncidencia;
import com.hotelaventura.entity.Incidencia;
import com.hotelaventura.entity.PrioridadIncidencia;
import com.hotelaventura.repository.IncidenciaRepository;
import com.hotelaventura.service.IncidenciaService;
import com.hotelaventura.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenciaServiceImpl implements IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final HabitacionRepository habitacionRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public IncidenciaDTO reportar(IncidenciaDTO dto, String usuario) {
        Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion no encontrada con ID: " + dto.getHabitacionId()));

        PrioridadIncidencia prioridad;
        try {
            prioridad = PrioridadIncidencia.valueOf(dto.getPrioridad().toUpperCase());
        } catch (Exception e) {
            prioridad = PrioridadIncidencia.MEDIA;
        }

        Incidencia incidencia = Incidencia.builder()
                .habitacion(habitacion)
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .prioridad(prioridad)
                .estado(EstadoIncidencia.REPORTADA)
                .reportadaPor(dto.getReportadaPor() != null ? dto.getReportadaPor() : usuario)
                .fechaReporte(LocalDateTime.now())
                .fotoEvidenciaUrl(dto.getFotoEvidenciaUrl())
                .build();

        // Si la averia es grave, sacamos el cuarto de servicio para que recepcion no lo venda
        if (prioridad == PrioridadIncidencia.URGENTE || prioridad == PrioridadIncidencia.ALTA) {
            habitacion.setEstadoOperativo(EstadoOperativo.FUERA_DE_SERVICIO);
            habitacionRepository.save(habitacion);
        }

        Incidencia guardada = incidenciaRepository.save(incidencia);

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "MANTENIMIENTO",
                "REPORTAR_INCIDENCIA",
                "Incidencia",
                guardada.getId(),
                "Averia reportada en habitacion " + habitacion.getNumero() + " (" + prioridad + "): " + dto.getTitulo(),
                "127.0.0.1"
        );

        return convertirADTO(guardada);
    }

    @Override
    @Transactional
    public IncidenciaDTO asignarTecnico(Long id, String tecnico) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con ID: " + id));

        incidencia.setAsignadaA(tecnico);
        incidencia.setEstado(EstadoIncidencia.EN_REPARACION);

        return convertirADTO(incidenciaRepository.save(incidencia));
    }

    @Override
    @Transactional
    public IncidenciaDTO resolver(Long id, String solucion, String usuario) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con ID: " + id));

        incidencia.setEstado(EstadoIncidencia.RESUELTA);
        incidencia.setFechaSolucion(LocalDateTime.now());
        incidencia.setSolucionAplicada(solucion);

        Incidencia guardada = incidenciaRepository.save(incidencia);

        // Ya que el tecnico lo soluciono, reactivamos el cuarto para que vuelva a estar disponible
        Habitacion habitacion = incidencia.getHabitacion();
        if (habitacion.getEstadoOperativo() == EstadoOperativo.FUERA_DE_SERVICIO) {
            habitacion.setEstadoOperativo(EstadoOperativo.DISPONIBLE);
            habitacionRepository.save(habitacion);
        }

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "MANTENIMIENTO",
                "RESOLVER_INCIDENCIA",
                "Incidencia",
                guardada.getId(),
                "Averia resuelta en habitacion " + habitacion.getNumero() + ". Habitacion habilitada nuevamente.",
                "127.0.0.1"
        );

        return convertirADTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidenciaDTO obtenerPorId(Long id) {
        return incidenciaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> listarTodas() {
        return incidenciaRepository.findAllByOrderByFechaReporteDesc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> listarPorHabitacion(Long habitacionId) {
        return incidenciaRepository.findByHabitacionId(habitacionId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> listarPendientes() {
        return incidenciaRepository.findByEstado(EstadoIncidencia.REPORTADA).stream()
                .map(this::convertirADTO)
                .toList();
    }

    private IncidenciaDTO convertirADTO(Incidencia i) {
        return IncidenciaDTO.builder()
                .id(i.getId())
                .habitacionId(i.getHabitacion().getId())
                .habitacionNumero(i.getHabitacion().getNumero())
                .titulo(i.getTitulo())
                .descripcion(i.getDescripcion())
                .prioridad(i.getPrioridad().name())
                .estado(i.getEstado().name())
                .reportadaPor(i.getReportadaPor())
                .asignadaA(i.getAsignadaA())
                .fechaReporte(i.getFechaReporte())
                .fechaSolucion(i.getFechaSolucion())
                .solucionAplicada(i.getSolucionAplicada())
                .fotoEvidenciaUrl(i.getFotoEvidenciaUrl())
                .build();
    }
}
