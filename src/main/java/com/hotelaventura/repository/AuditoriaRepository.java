package com.hotelaventura.repository;

import com.hotelaventura.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByUsuarioOrderByFechaHoraDesc(String usuario);
    List<Auditoria> findByAccionOrderByFechaHoraDesc(String accion);
    List<Auditoria> findAllByOrderByFechaHoraDesc();
}
