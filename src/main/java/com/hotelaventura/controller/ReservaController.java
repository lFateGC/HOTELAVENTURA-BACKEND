package com.hotelaventura.controller;

import com.hotelaventura.dto.ReservaRequestDTO;
import com.hotelaventura.dto.ReservaResponseDTO;
import com.hotelaventura.entity.EstadoReserva;
import com.hotelaventura.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/llegadas-hoy")
    public ResponseEntity<List<ReservaResponseDTO>> listarLlegadasHoy(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(reservaService.listarLlegadasHoy(fecha));
    }

    @GetMapping("/salidas-hoy")
    public ResponseEntity<List<ReservaResponseDTO>> listarSalidasHoy(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(reservaService.listarSalidasHoy(fecha));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorEstado(@PathVariable EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(reservaService.obtenerPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @RequestBody ReservaRequestDTO request,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return new ResponseEntity<>(reservaService.crear(request, usuario), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return ResponseEntity.ok(reservaService.cancelar(id, motivo, usuario));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return ResponseEntity.ok(reservaService.confirmar(id, usuario));
    }
}
