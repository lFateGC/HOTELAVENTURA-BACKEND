package com.hotelaventura.repository;

import com.hotelaventura.entity.CategoriaProducto;
import com.hotelaventura.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByNombre(String nombre);
    List<Producto> findByCategoria(CategoriaProducto categoria);
    List<Producto> findByActivoTrue();
    List<Producto> findAllByOrderByNombreAsc();
}
