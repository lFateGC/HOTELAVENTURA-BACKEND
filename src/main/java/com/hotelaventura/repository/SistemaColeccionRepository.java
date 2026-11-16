package com.hotelaventura.repository;

import com.hotelaventura.entity.SistemaColeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SistemaColeccionRepository extends JpaRepository<SistemaColeccion, String> {
}
