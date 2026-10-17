package com.hotelaventura.modulo_seguridad.repository;

import com.hotelaventura.modulo_seguridad.entity.Rol;
import com.hotelaventura.modulo_seguridad.entity.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RolNombre nombre);
}
