package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.dto.CargoDTO;
import com.hotelaventura.dto.PagoDTO;
import com.hotelaventura.entity.*;
import com.hotelaventura.repository.CargoRepository;
import com.hotelaventura.repository.FolioRepository;
import com.hotelaventura.repository.PagoRepository;
import com.hotelaventura.service.FolioService;
import com.hotelaventura.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolioServiceImpl implements FolioService {

    private final FolioRepository folioRepository;
    private final CargoRepository cargoRepository;
    private final PagoRepository pagoRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public Folio crearParaEstancia(Long estanciaId) {
        return folioRepository.findByEstanciaId(estanciaId)
                .orElseGet(() -> {
                    Folio folio = Folio.builder()
                            .estanciaId(estanciaId)
                            .estado(EstadoFolio.ABIERTO)
                            .totalCargos(BigDecimal.ZERO)
                            .totalAbonos(BigDecimal.ZERO)
                            .saldo(BigDecimal.ZERO)
                            .fechaApertura(LocalDateTime.now())
                            .build();
                    return folioRepository.save(folio);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Folio obtenerPorEstanciaId(Long estanciaId) {
        return folioRepository.findByEstanciaId(estanciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Folio no encontrado para la estancia: " + estanciaId));
    }

    @Override
    @Transactional(readOnly = true)
    public Folio obtenerPorId(Long id) {
        return folioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folio no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Cargo agregarCargo(CargoDTO dto, String usuario) {
        Folio folio = folioRepository.findById(dto.getFolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Folio no encontrado con ID: " + dto.getFolioId()));

        if (folio.getEstado() == EstadoFolio.CERRADO) {
            throw new BadRequestException("No se pueden agregar cargos a un folio que ya esta CERRADO");
        }

        CategoriaCargo categoria;
        try {
            categoria = CategoriaCargo.valueOf(dto.getCategoria().toUpperCase());
        } catch (Exception e) {
            categoria = CategoriaCargo.CONSUMO_POS;
        }

        int cantidad = dto.getCantidad() != null && dto.getCantidad() > 0 ? dto.getCantidad() : 1;
        BigDecimal totalCargo = dto.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));

        Cargo cargo = Cargo.builder()
                .folio(folio)
                .concepto(dto.getConcepto())
                .categoria(categoria)
                .cantidad(cantidad)
                .precioUnitario(dto.getPrecioUnitario())
                .montoTotal(totalCargo)
                .fecha(LocalDateTime.now())
                .anulado(false)
                .build();

        Cargo guardado = cargoRepository.save(cargo);

        // Actualizar saldo del folio
        folio.setTotalCargos(folio.getTotalCargos().add(totalCargo));
        folio.setSaldo(folio.getTotalCargos().subtract(folio.getTotalAbonos()));
        folioRepository.save(folio);

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "SISTEMA",
                "CARGO_FOLIO",
                "Folio",
                folio.getId(),
                "Cargo agregado al folio #" + folio.getId() + ": " + dto.getConcepto() + " (S/ " + totalCargo + ")",
                "127.0.0.1"
        );

        return guardado;
    }

    @Override
    @Transactional
    public Pago registrarPago(PagoDTO dto, String usuario) {
        Folio folio = folioRepository.findById(dto.getFolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Folio no encontrado con ID: " + dto.getFolioId()));

        if (folio.getEstado() == EstadoFolio.CERRADO) {
            throw new BadRequestException("No se pueden registrar pagos en un folio CERRADO");
        }

        MetodoPago metodo;
        try {
            metodo = MetodoPago.valueOf(dto.getMetodo().toUpperCase());
        } catch (Exception e) {
            metodo = MetodoPago.EFECTIVO;
        }

        Pago pago = Pago.builder()
                .folio(folio)
                .metodo(metodo)
                .monto(dto.getMonto())
                .referencia(dto.getReferencia())
                .turnoCajaId(dto.getTurnoCajaId())
                .fecha(LocalDateTime.now())
                .build();

        Pago guardado = pagoRepository.save(pago);

        // Actualizar abonos y saldo del folio
        folio.setTotalAbonos(folio.getTotalAbonos().add(dto.getMonto()));
        folio.setSaldo(folio.getTotalCargos().subtract(folio.getTotalAbonos()));
        folioRepository.save(folio);

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "CAJERO",
                "PAGO_FOLIO",
                "Folio",
                folio.getId(),
                "Pago registrado en folio #" + folio.getId() + " (" + metodo + "): S/ " + dto.getMonto(),
                "127.0.0.1"
        );

        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cargo> listarCargosPorFolio(Long folioId) {
        return cargoRepository.findByFolioIdAndAnuladoFalse(folioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> listarPagosPorFolio(Long folioId) {
        return pagoRepository.findByFolioId(folioId);
    }

    @Override
    @Transactional
    public Folio cerrarFolio(Long folioId, String usuario) {
        Folio folio = folioRepository.findById(folioId)
                .orElseThrow(() -> new ResourceNotFoundException("Folio no encontrado con ID: " + folioId));

        if (folio.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("No se puede cerrar el folio. Queda un saldo pendiente de pago de: S/ " + folio.getSaldo());
        }

        folio.setEstado(EstadoFolio.CERRADO);
        folio.setFechaCierre(LocalDateTime.now());
        Folio guardado = folioRepository.save(folio);

        auditoriaService.registrarAccion(
                usuario != null ? usuario : "RECEPCION",
                "CERRAR_FOLIO",
                "Folio",
                guardado.getId(),
                "Folio #" + guardado.getId() + " cerrado satisfactoriamente con saldo en cero.",
                "127.0.0.1"
        );

        return guardado;
    }
}
