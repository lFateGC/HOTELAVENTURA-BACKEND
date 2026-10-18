package com.hotelaventura.repository;

import com.hotelaventura.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFolioId(Long folioId);
    List<Pago> findByTurnoCajaId(Long turnoCajaId);
}
