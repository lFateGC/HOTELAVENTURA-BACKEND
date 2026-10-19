package com.hotelaventura.repository;

import com.hotelaventura.entity.RegistroHuesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroHuespedRepository extends JpaRepository<RegistroHuesped, Long> {
    List<RegistroHuesped> findByEstanciaId(Long estanciaId);
    List<RegistroHuesped> findByHuespedId(Long huespedId);
}
