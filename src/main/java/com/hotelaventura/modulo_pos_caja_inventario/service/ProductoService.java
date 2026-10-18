package com.hotelaventura.modulo_pos_caja_inventario.service;

import com.hotelaventura.modulo_pos_caja_inventario.dto.ProductoDTO;

import java.util.List;

public interface ProductoService {
    ProductoDTO crear(ProductoDTO dto);
    ProductoDTO actualizar(Long id, ProductoDTO dto);
    ProductoDTO obtenerPorId(Long id);
    List<ProductoDTO> listarTodos();
    List<ProductoDTO> listarActivos();
    ProductoDTO actualizarStock(Long id, int cantidadCambio);
}
