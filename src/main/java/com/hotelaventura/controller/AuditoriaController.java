package com.hotelaventura.controller;

import com.hotelaventura.dto.AuditoriaDTO;
import com.hotelaventura.service.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaDTO>> listarTodas() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }

    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<AuditoriaDTO>> listarPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuario));
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<List<AuditoriaDTO>> listarPorAccion(@PathVariable String accion) {
        return ResponseEntity.ok(auditoriaService.listarPorAccion(accion));
    }
}
