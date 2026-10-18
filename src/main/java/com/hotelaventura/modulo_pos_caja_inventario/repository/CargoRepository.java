package com.hotelaventura.modulo_pos_caja_inventario.repository;

import com.hotelaventura.modulo_pos_caja_inventario.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    List<Cargo> findByFolioIdAndAnuladoFalse(Long folioId);
    List<Cargo> findByFolioId(Long folioId);
}
