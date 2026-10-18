package com.hotelaventura.repository;

import com.hotelaventura.entity.EstadoEstancia;
import com.hotelaventura.entity.Estancia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstanciaRepository extends JpaRepository<Estancia, Long> {
    Optional<Estancia> findByCodigo(String codigo);
    Optional<Estancia> findByHabitacionIdAndEstado(Long habitacionId, EstadoEstancia estado);
    List<Estancia> findByEstado(EstadoEstancia estado);
    List<Estancia> findAllByOrderByCheckInRealDesc();
    long countByEstado(EstadoEstancia estado);
}
