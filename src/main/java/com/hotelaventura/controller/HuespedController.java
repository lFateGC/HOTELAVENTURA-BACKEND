package com.hotelaventura.controller;

import com.hotelaventura.dto.HuespedDTO;
import com.hotelaventura.dto.RegistroHuespedDTO;
import com.hotelaventura.service.HuespedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@RequiredArgsConstructor
public class HuespedController {

    private final HuespedService huespedService;

    @GetMapping
    public ResponseEntity<List<HuespedDTO>> listarTodos(@RequestParam(required = false) String buscar) {
        if (buscar != null && !buscar.isBlank()) {
            return ResponseEntity.ok(huespedService.buscarPorNombre(buscar));
        }
        return ResponseEntity.ok(huespedService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HuespedDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(huespedService.obtenerPorId(id));
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<HuespedDTO> obtenerPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(huespedService.obtenerPorDocumento(documento));
    }

    @PostMapping
    public ResponseEntity<HuespedDTO> registrar(@Valid @RequestBody HuespedDTO dto) {
        return new ResponseEntity<>(huespedService.registrar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HuespedDTO> actualizar(@PathVariable Long id, @Valid @RequestBody HuespedDTO dto) {
        return ResponseEntity.ok(huespedService.actualizar(id, dto));
    }

    @PostMapping("/registro-estancia")
    public ResponseEntity<RegistroHuespedDTO> registrarEnEstancia(@Valid @RequestBody RegistroHuespedDTO dto) {
        return new ResponseEntity<>(huespedService.registrarEnEstancia(dto), HttpStatus.CREATED);
    }

    @GetMapping("/estancia/{estanciaId}")
    public ResponseEntity<List<RegistroHuespedDTO>> listarPorEstancia(@PathVariable Long estanciaId) {
        return ResponseEntity.ok(huespedService.listarPorEstancia(estanciaId));
    }
}
