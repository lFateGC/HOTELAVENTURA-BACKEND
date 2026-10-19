package com.hotelaventura.repository;

import com.hotelaventura.entity.VentaPOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VentaPOSRepository extends JpaRepository<VentaPOS, Long> {
    Optional<VentaPOS> findByCodigo(String codigo);
    List<VentaPOS> findByFolioId(Long folioId);
    List<VentaPOS> findAllByOrderByFechaDesc();
}
