package com.hotelaventura.repository;

import com.hotelaventura.entity.EstadoLimpieza;
import com.hotelaventura.entity.EstadoOperativo;
import com.hotelaventura.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    Optional<Habitacion> findByNumero(String numero);
    boolean existsByNumero(String numero);
    List<Habitacion> findByEstadoOperativo(EstadoOperativo estadoOperativo);
    List<Habitacion> findByEstadoLimpieza(EstadoLimpieza estadoLimpieza);
    List<Habitacion> findByPisoId(Long pisoId);
    List<Habitacion> findByTipoHabitacionId(Long tipoHabitacionId);
    List<Habitacion> findAllByOrderByNumeroAsc();
    long countByEstadoOperativo(EstadoOperativo estadoOperativo);
    long countByEstadoLimpieza(EstadoLimpieza estadoLimpieza);
}
