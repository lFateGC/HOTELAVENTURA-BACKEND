package com.hotelaventura.service;

import com.hotelaventura.dto.ProductoDTO;

import java.util.List;

public interface ProductoService {
    ProductoDTO crear(ProductoDTO dto);
    ProductoDTO actualizar(Long id, ProductoDTO dto);
    ProductoDTO obtenerPorId(Long id);
    List<ProductoDTO> listarTodos();
    List<ProductoDTO> listarActivos();
    ProductoDTO actualizarStock(Long id, int cantidadCambio);
}
