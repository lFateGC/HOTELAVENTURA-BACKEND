package com.hotelaventura.modulo_pos_caja_inventario.controller;

import com.hotelaventura.modulo_pos_caja_inventario.dto.VentaPOSDTO;
import com.hotelaventura.modulo_pos_caja_inventario.service.VentaPOSService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class VentaPOSController {

    private final VentaPOSService ventaPOSService;

    @GetMapping("/ventas")
    public ResponseEntity<List<VentaPOSDTO>> listarTodas() {
        return ResponseEntity.ok(ventaPOSService.listarTodas());
    }

    @GetMapping("/ventas/{id}")
    public ResponseEntity<VentaPOSDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaPOSService.obtenerPorId(id));
    }

    @PostMapping("/ventas")
    public ResponseEntity<VentaPOSDTO> procesarVenta(
            @Valid @RequestBody VentaPOSDTO dto,
            @RequestParam(defaultValue = "CAJERO") String usuario
    ) {
        return new ResponseEntity<>(ventaPOSService.procesarVenta(dto, usuario), HttpStatus.CREATED);
    }
}
