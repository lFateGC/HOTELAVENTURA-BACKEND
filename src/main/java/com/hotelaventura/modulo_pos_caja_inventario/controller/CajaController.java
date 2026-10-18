package com.hotelaventura.modulo_pos_caja_inventario.controller;

import com.hotelaventura.modulo_pos_caja_inventario.dto.TurnoCajaDTO;
import com.hotelaventura.modulo_pos_caja_inventario.entity.TurnoCaja;
import com.hotelaventura.modulo_pos_caja_inventario.service.CajaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    @GetMapping("/turnos")
    public ResponseEntity<List<TurnoCaja>> listarTurnos() {
        return ResponseEntity.ok(cajaService.listarTurnos());
    }

    @GetMapping("/turno-actual")
    public ResponseEntity<TurnoCaja> obtenerTurnoActual() {
        return ResponseEntity.ok(cajaService.obtenerTurnoActual());
    }

    @PostMapping("/abrir")
    public ResponseEntity<TurnoCaja> abrirTurno(
            @Valid @RequestBody TurnoCajaDTO dto,
            @RequestParam(defaultValue = "CAJERO") String cajero
    ) {
        return new ResponseEntity<>(cajaService.abrirTurno(dto, cajero), HttpStatus.CREATED);
    }

    @PatchMapping("/cerrar/{id}")
    public ResponseEntity<TurnoCaja> cerrarTurno(
            @PathVariable Long id,
            @RequestParam BigDecimal montoFinalContado,
            @RequestParam(required = false) String observaciones,
            @RequestParam(defaultValue = "CAJERO") String cajero
    ) {
        return ResponseEntity.ok(cajaService.cerrarTurno(id, montoFinalContado, observaciones, cajero));
    }
}
