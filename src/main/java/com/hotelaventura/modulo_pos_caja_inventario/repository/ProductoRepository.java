package com.hotelaventura.modulo_pos_caja_inventario.repository;

import com.hotelaventura.modulo_pos_caja_inventario.entity.CategoriaProducto;
import com.hotelaventura.modulo_pos_caja_inventario.entity.Producto;
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
