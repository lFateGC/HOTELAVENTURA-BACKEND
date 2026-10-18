package com.hotelaventura.modulo_pos_caja_inventario.repository;

import com.hotelaventura.modulo_pos_caja_inventario.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFolioId(Long folioId);
    List<Pago> findByTurnoCajaId(Long turnoCajaId);
}
