package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.dto.ProductoDTO;
import com.hotelaventura.entity.CategoriaProducto;
import com.hotelaventura.entity.Producto;
import com.hotelaventura.repository.ProductoRepository;
import com.hotelaventura.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public ProductoDTO crear(ProductoDTO dto) {
        if (productoRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new BadRequestException("Ya existe un producto con el nombre: " + dto.getNombre());
        }

        CategoriaProducto categoria;
        try {
            categoria = CategoriaProducto.valueOf(dto.getCategoria().toUpperCase());
        } catch (Exception e) {
            categoria = CategoriaProducto.BEBIDAS;
        }

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .categoria(categoria)
                .precioVenta(dto.getPrecioVenta())
                .costoUnitario(dto.getCostoUnitario())
                .stockActual(dto.getStockActual() != null ? dto.getStockActual() : 0)
                .stockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5)
                .controlaStock(dto.getControlaStock() != null ? dto.getControlaStock() : true)
                .activo(true)
                .imagenUrl(dto.getImagenUrl())
                .build();

        return convertirADTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        producto.setNombre(dto.getNombre());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setCostoUnitario(dto.getCostoUnitario());
        producto.setStockMinimo(dto.getStockMinimo());

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }
        if (dto.getImagenUrl() != null) {
            producto.setImagenUrl(dto.getImagenUrl());
        }

        return convertirADTO(productoRepository.save(producto));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional
    public ProductoDTO actualizarStock(Long id, int cantidadCambio) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        int nuevoStock = producto.getStockActual() + cantidadCambio;
        if (nuevoStock < 0) {
            throw new BadRequestException("Stock insuficiente para el producto: " + producto.getNombre() + " (Actual: " + producto.getStockActual() + ")");
        }

        producto.setStockActual(nuevoStock);
        return convertirADTO(productoRepository.save(producto));
    }

    private ProductoDTO convertirADTO(Producto p) {
        return ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .categoria(p.getCategoria().name())
                .precioVenta(p.getPrecioVenta())
                .costoUnitario(p.getCostoUnitario())
                .stockActual(p.getStockActual())
                .stockMinimo(p.getStockMinimo())
                .controlaStock(p.getControlaStock())
                .activo(p.getActivo())
                .imagenUrl(p.getImagenUrl())
                .build();
    }
}
