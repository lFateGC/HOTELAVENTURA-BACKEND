package com.hotelaventura.modulo_pos_caja_inventario.repository;

import com.hotelaventura.modulo_pos_caja_inventario.entity.EstadoCaja;
import com.hotelaventura.modulo_pos_caja_inventario.entity.TurnoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoCajaRepository extends JpaRepository<TurnoCaja, Long> {
    Optional<TurnoCaja> findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja estado);
    Optional<TurnoCaja> findFirstByCajeroAndEstado(String cajero, EstadoCaja estado);
    List<TurnoCaja> findAllByOrderByFechaAperturaDesc();
}
