package com.hotelaventura.repository;

import com.hotelaventura.entity.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, Long> {
    Optional<Huesped> findByNumeroDocumento(String numeroDocumento);
    Optional<Huesped> findByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
    List<Huesped> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
    List<Huesped> findAllByOrderByApellidosAsc();
}
