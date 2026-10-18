package com.hotelaventura.repository;

import com.hotelaventura.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    List<Cargo> findByFolioIdAndAnuladoFalse(Long folioId);
    List<Cargo> findByFolioId(Long folioId);
}
