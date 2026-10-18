package com.hotelaventura.controller;

import com.hotelaventura.dto.CargoDTO;
import com.hotelaventura.dto.PagoDTO;
import com.hotelaventura.entity.Cargo;
import com.hotelaventura.entity.Folio;
import com.hotelaventura.entity.Pago;
import com.hotelaventura.service.FolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folios")
@RequiredArgsConstructor
public class FolioController {

    private final FolioService folioService;

    @GetMapping("/estancia/{estanciaId}")
    public ResponseEntity<Folio> obtenerPorEstancia(@PathVariable Long estanciaId) {
        return ResponseEntity.ok(folioService.obtenerPorEstanciaId(estanciaId));
    }

    @PostMapping("/estancia/{estanciaId}")
    public ResponseEntity<Folio> crearParaEstancia(@PathVariable Long estanciaId) {
        return new ResponseEntity<>(folioService.crearParaEstancia(estanciaId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Folio> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(folioService.obtenerPorId(id));
    }

    @GetMapping("/{id}/cargos")
    public ResponseEntity<List<Cargo>> listarCargos(@PathVariable Long id) {
        return ResponseEntity.ok(folioService.listarCargosPorFolio(id));
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<List<Pago>> listarPagos(@PathVariable Long id) {
        return ResponseEntity.ok(folioService.listarPagosPorFolio(id));
    }

    @PostMapping("/cargos")
    public ResponseEntity<Cargo> agregarCargo(
            @Valid @RequestBody CargoDTO dto,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return new ResponseEntity<>(folioService.agregarCargo(dto, usuario), HttpStatus.CREATED);
    }

    @PostMapping("/pagos")
    public ResponseEntity<Pago> registrarPago(
            @Valid @RequestBody PagoDTO dto,
            @RequestParam(defaultValue = "CAJERO") String usuario
    ) {
        return new ResponseEntity<>(folioService.registrarPago(dto, usuario), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Folio> cerrarFolio(
            @PathVariable Long id,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return ResponseEntity.ok(folioService.cerrarFolio(id, usuario));
    }
}
