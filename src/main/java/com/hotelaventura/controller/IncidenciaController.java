package com.hotelaventura.controller;

import com.hotelaventura.dto.IncidenciaDTO;
import com.hotelaventura.service.IncidenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @GetMapping
    public ResponseEntity<List<IncidenciaDTO>> listarTodas() {
        return ResponseEntity.ok(incidenciaService.listarTodas());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<IncidenciaDTO>> listarPendientes() {
        return ResponseEntity.ok(incidenciaService.listarPendientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(incidenciaService.obtenerPorId(id));
    }

    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<IncidenciaDTO>> listarPorHabitacion(@PathVariable Long habitacionId) {
        return ResponseEntity.ok(incidenciaService.listarPorHabitacion(habitacionId));
    }

    @PostMapping
    public ResponseEntity<IncidenciaDTO> reportar(
            @Valid @RequestBody IncidenciaDTO dto,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return new ResponseEntity<>(incidenciaService.reportar(dto, usuario), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/asignar")
    public ResponseEntity<IncidenciaDTO> asignar(
            @PathVariable Long id,
            @RequestParam String tecnico
    ) {
        return ResponseEntity.ok(incidenciaService.asignarTecnico(id, tecnico));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<IncidenciaDTO> resolver(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Problema solucionado con exito") String solucion,
            @RequestParam(defaultValue = "MANTENIMIENTO") String usuario
    ) {
        return ResponseEntity.ok(incidenciaService.resolver(id, solucion, usuario));
    }
}
