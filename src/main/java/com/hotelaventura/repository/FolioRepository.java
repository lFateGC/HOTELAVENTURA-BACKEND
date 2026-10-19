package com.hotelaventura.repository;

import com.hotelaventura.entity.EstadoFolio;
import com.hotelaventura.entity.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<Folio, Long> {
    Optional<Folio> findByEstanciaId(Long estanciaId);
    List<Folio> findByEstado(EstadoFolio estado);
}
