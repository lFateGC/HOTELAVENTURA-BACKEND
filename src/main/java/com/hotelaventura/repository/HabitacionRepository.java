package com.hotelaventura.modulo_habitaciones_reservas.repository;

import com.hotelaventura.modulo_habitaciones_reservas.entity.EstadoLimpieza;
import com.hotelaventura.modulo_habitaciones_reservas.entity.EstadoOperativo;
import com.hotelaventura.modulo_habitaciones_reservas.entity.Habitacion;
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
