package com.hotelaventura.modulo_pos_caja_inventario.repository;

import com.hotelaventura.modulo_pos_caja_inventario.entity.EstadoFolio;
import com.hotelaventura.modulo_pos_caja_inventario.entity.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<Folio, Long> {
    Optional<Folio> findByEstanciaId(Long estanciaId);
    List<Folio> findByEstado(EstadoFolio estado);
}
