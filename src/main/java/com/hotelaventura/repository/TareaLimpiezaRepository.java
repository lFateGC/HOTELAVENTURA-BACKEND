package com.hotelaventura.repository;

import com.hotelaventura.entity.EstadoLimpiezaTarea;
import com.hotelaventura.entity.TareaLimpieza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaLimpiezaRepository extends JpaRepository<TareaLimpieza, Long> {
    List<TareaLimpieza> findByEstado(EstadoLimpiezaTarea estado);
    List<TareaLimpieza> findByHabitacionId(Long habitacionId);
    List<TareaLimpieza> findAllByOrderByFechaHoraCreacionDesc();
}
