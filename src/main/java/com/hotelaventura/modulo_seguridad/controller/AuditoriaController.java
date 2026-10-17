package com.hotelaventura.modulo_seguridad.controller;

import com.hotelaventura.modulo_seguridad.dto.AuditoriaDTO;
import com.hotelaventura.modulo_seguridad.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<AuditoriaDTO>> listarTodo() {
        return ResponseEntity.ok(auditoriaService.listarTodo());
    }

    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<AuditoriaDTO>> listarPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuario));
    }

    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<AuditoriaDTO>> listarPorEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad));
    }
}
