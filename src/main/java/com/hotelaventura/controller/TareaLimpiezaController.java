package com.hotelaventura.controller;

import com.hotelaventura.dto.TareaLimpiezaDTO;
import com.hotelaventura.service.TareaLimpiezaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/limpieza")
@RequiredArgsConstructor
public class TareaLimpiezaController {

    private final TareaLimpiezaService tareaLimpiezaService;

    @GetMapping
    public ResponseEntity<List<TareaLimpiezaDTO>> listarTodas() {
        return ResponseEntity.ok(tareaLimpiezaService.listarTodas());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<TareaLimpiezaDTO>> listarPendientes() {
        return ResponseEntity.ok(tareaLimpiezaService.listarPendientes());
    }

    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<TareaLimpiezaDTO>> listarPorHabitacion(@PathVariable Long habitacionId) {
        return ResponseEntity.ok(tareaLimpiezaService.listarPorHabitacion(habitacionId));
    }

    @PostMapping
    public ResponseEntity<TareaLimpiezaDTO> crear(
            @Valid @RequestBody TareaLimpiezaDTO dto,
            @RequestParam(defaultValue = "HOUSEKEEPING") String usuario
    ) {
        return new ResponseEntity<>(tareaLimpiezaService.crear(dto, usuario), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<TareaLimpiezaDTO> iniciar(
            @PathVariable Long id,
            @RequestParam(required = false) String operario
    ) {
        return ResponseEntity.ok(tareaLimpiezaService.iniciarTarea(id, operario));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<TareaLimpiezaDTO> completar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "HOUSEKEEPING") String operario
    ) {
        return ResponseEntity.ok(tareaLimpiezaService.completarTarea(id, operario));
    }
}
