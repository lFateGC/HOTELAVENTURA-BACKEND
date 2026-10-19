package com.hotelaventura.controller;

import com.hotelaventura.dto.HabitacionDTO;
import com.hotelaventura.entity.EstadoLimpieza;
import com.hotelaventura.entity.EstadoOperativo;
import com.hotelaventura.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<HabitacionDTO>> listarTodas() {
        return ResponseEntity.ok(habitacionService.listarTodas());
    }

    @GetMapping("/rack")
    public ResponseEntity<List<HabitacionDTO>> listarParaRack() {
        return ResponseEntity.ok(habitacionService.listarParaRack());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(habitacionService.obtenerPorId(id));
    }

    @GetMapping("/piso/{pisoId}")
    public ResponseEntity<List<HabitacionDTO>> listarPorPiso(@PathVariable Long pisoId) {
        return ResponseEntity.ok(habitacionService.listarPorPiso(pisoId));
    }

    @PostMapping
    public ResponseEntity<HabitacionDTO> crear(@Valid @RequestBody HabitacionDTO dto) {
        return new ResponseEntity<>(habitacionService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionDTO> actualizar(@PathVariable Long id, @Valid @RequestBody HabitacionDTO dto) {
        return ResponseEntity.ok(habitacionService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado-operativo")
    public ResponseEntity<HabitacionDTO> cambiarEstadoOperativo(
            @PathVariable Long id,
            @RequestParam EstadoOperativo estado,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return ResponseEntity.ok(habitacionService.cambiarEstadoOperativo(id, estado, usuario));
    }

    @PatchMapping("/{id}/estado-limpieza")
    public ResponseEntity<HabitacionDTO> cambiarEstadoLimpieza(
            @PathVariable Long id,
            @RequestParam EstadoLimpieza estado,
            @RequestParam(defaultValue = "HOUSEKEEPING") String usuario
    ) {
        return ResponseEntity.ok(habitacionService.cambiarEstadoLimpieza(id, estado, usuario));
    }
}
