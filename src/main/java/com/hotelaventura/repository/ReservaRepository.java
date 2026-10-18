package com.hotelaventura.repository;

import com.hotelaventura.entity.EstadoReserva;
import com.hotelaventura.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findByCodigo(String codigo);
    List<Reserva> findByEstado(EstadoReserva estado);
    List<Reserva> findByFechaLlegada(LocalDate fechaLlegada);
    List<Reserva> findByDocumentoTitular(String documentoTitular);
    List<Reserva> findAllByOrderByFechaCreacionDesc();
    long countByEstado(EstadoReserva estado);
}
