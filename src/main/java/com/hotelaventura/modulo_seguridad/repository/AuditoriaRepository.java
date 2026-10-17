package com.hotelaventura.modulo_seguridad.repository;

import com.hotelaventura.modulo_seguridad.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByUsuarioOrderByFechaHoraDesc(String usuario);
    List<Auditoria> findByEntidadOrderByFechaHoraDesc(String entidad);
    List<Auditoria> findAllByOrderByFechaHoraDesc();
}
