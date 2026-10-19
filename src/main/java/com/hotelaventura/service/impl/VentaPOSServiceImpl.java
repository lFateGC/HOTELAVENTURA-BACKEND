package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.dto.CargoDTO;
import com.hotelaventura.dto.VentaPOSDTO;
import com.hotelaventura.entity.*;
import com.hotelaventura.repository.ProductoRepository;
import com.hotelaventura.repository.TurnoCajaRepository;
import com.hotelaventura.repository.VentaPOSRepository;
import com.hotelaventura.service.FolioService;
import com.hotelaventura.service.VentaPOSService;
import com.hotelaventura.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaPOSServiceImpl implements VentaPOSService {

    private final VentaPOSRepository ventaPOSRepository;
    private final ProductoRepository productoRepository;
    private final TurnoCajaRepository turnoCajaRepository;
    private final FolioService folioService;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public VentaPOSDTO procesarVenta(VentaPOSDTO dto, String usuario) {
        MetodoPago metodoTemp;
        try {
            metodoTemp = MetodoPago.valueOf(dto.getMetodoPago().toUpperCase());
        } catch (Exception e) {
            metodoTemp = MetodoPago.EFECTIVO;
        }
        final MetodoPago metodo = metodoTemp;

        String codigo = "POS-" + System.currentTimeMillis() % 1000000;
        BigDecimal montoTotal = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();
        List<VentaPOSDTO.ItemVentaDTO> responseItems = new ArrayList<>();

        VentaPOS venta = VentaPOS.builder()
                .codigo(codigo)
                .vendedor(usuario != null ? usuario : "CAJERO")
                .folioId(dto.getFolioId())
                .habitacionNumero(dto.getHabitacionNumero())
                .metodoPago(metodo)
                .fecha(LocalDateTime.now())
                .detalles(new ArrayList<>())
                .build();

        for (VentaPOSDTO.ItemVentaDTO item : dto.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + item.getProductoId()));

            if (Boolean.TRUE.equals(producto.getControlaStock())) {
                if (producto.getStockActual() < item.getCantidad()) {
                    throw new BadRequestException("Stock insuficiente para: " + producto.getNombre() + " (Disponible: " + producto.getStockActual() + ")");
                }
                producto.setStockActual(producto.getStockActual() - item.getCantidad());
                productoRepository.save(producto);
            }

            BigDecimal precio = producto.getPrecioVenta();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(item.getCantidad()));
            montoTotal = montoTotal.add(subtotal);

            DetalleVenta detalle = DetalleVenta.builder()
                    .ventaPOS(venta)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build();

            detalles.add(detalle);

            responseItems.add(VentaPOSDTO.ItemVentaDTO.builder()
                    .productoId(producto.getId())
                    .productoNombre(producto.getNombre())
                    .cantidad(item.getCantidad())
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build());
        }

        venta.setMontoTotal(montoTotal);
        venta.setDetalles(detalles);
        VentaPOS guardada = ventaPOSRepository.save(venta);

        // Si el cliente pide cargarlo a la habitacion, se lo agregamos a su folio
        if (dto.getFolioId() != null) {
            CargoDTO cargoDTO = CargoDTO.builder()
                    .folioId(dto.getFolioId())
                    .concepto("Consumo POS #" + guardada.getCodigo())
                    .categoria("CONSUMO_POS")
                    .cantidad(1)
                    .precioUnitario(montoTotal)
                    .build();
            folioService.agregarCargo(cargoDTO, usuario);
        } else {
            // Si pagan al momento en mostrador, entra directo a la caja activa
            turnoCajaRepository.findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA).ifPresent(turno -> {
                if (metodo == MetodoPago.EFECTIVO) {
                    turno.setTotalVentasEfectivo(turno.getTotalVentasEfectivo().add(guardada.getMontoTotal()));
                } else {
                    turno.setTotalVentasDigital(turno.getTotalVentasDigital().add(guardada.getMontoTotal()));
                }
                turnoCajaRepository.save(turno);
            });
        }

        auditoriaService.registrarAccion(
                usuario,
                "VENTA_POS",
                "VentaPOS",
                guardada.getId(),
                "Venta POS completada #" + guardada.getCodigo() + " por S/ " + montoTotal + " (" + metodo + ")",
                "127.0.0.1"
        );

        dto.setId(guardada.getId());
        dto.setCodigo(guardada.getCodigo());
        dto.setMontoTotal(montoTotal);
        dto.setFecha(guardada.getFecha());
        dto.setItems(responseItems);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public VentaPOSDTO obtenerPorId(Long id) {
        VentaPOS v = ventaPOSRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));

        return convertirADTO(v);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaPOSDTO> listarTodas() {
        return ventaPOSRepository.findAllByOrderByFechaDesc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    private VentaPOSDTO convertirADTO(VentaPOS v) {
        List<VentaPOSDTO.ItemVentaDTO> items = v.getDetalles().stream()
                .map(d -> VentaPOSDTO.ItemVentaDTO.builder()
                        .productoId(d.getProducto().getId())
                        .productoNombre(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .toList();

        return VentaPOSDTO.builder()
                .id(v.getId())
                .codigo(v.getCodigo())
                .vendedor(v.getVendedor())
                .folioId(v.getFolioId())
                .habitacionNumero(v.getHabitacionNumero())
                .metodoPago(v.getMetodoPago().name())
                .montoTotal(v.getMontoTotal())
                .fecha(v.getFecha())
                .items(items)
                .build();
    }
}
