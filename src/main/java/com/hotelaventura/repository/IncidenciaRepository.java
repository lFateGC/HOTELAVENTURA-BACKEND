package com.hotelaventura.repository;

import com.hotelaventura.entity.EstadoIncidencia;
import com.hotelaventura.entity.Incidencia;
import com.hotelaventura.entity.PrioridadIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByEstado(EstadoIncidencia estado);
    List<Incidencia> findByPrioridad(PrioridadIncidencia prioridad);
    List<Incidencia> findByHabitacionId(Long habitacionId);
    List<Incidencia> findAllByOrderByFechaReporteDesc();
}
