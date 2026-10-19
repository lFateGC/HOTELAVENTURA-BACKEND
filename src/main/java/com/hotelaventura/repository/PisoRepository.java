package com.hotelaventura.repository;

import com.hotelaventura.entity.Piso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PisoRepository extends JpaRepository<Piso, Long> {
    Optional<Piso> findByNumero(Integer numero);
    List<Piso> findAllByOrderByNumeroAsc();
}
