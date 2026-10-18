package com.hotelaventura.modulo_habitaciones_reservas.repository;

import com.hotelaventura.modulo_habitaciones_reservas.entity.Piso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PisoRepository extends JpaRepository<Piso, Long> {
    Optional<Piso> findByNumero(Integer numero);
    List<Piso> findAllByOrderByNumeroAsc();
}
