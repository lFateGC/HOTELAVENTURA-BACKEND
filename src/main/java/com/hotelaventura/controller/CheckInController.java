package com.hotelaventura.controller;

import com.hotelaventura.dto.CheckInRequestDTO;
import com.hotelaventura.dto.CheckOutRequestDTO;
import com.hotelaventura.entity.Estancia;
import com.hotelaventura.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estancias")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping
    public ResponseEntity<List<Estancia>> listarTodas() {
        return ResponseEntity.ok(checkInService.listarTodas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Estancia>> listarActivas() {
        return ResponseEntity.ok(checkInService.listarEstanciasActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estancia> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(checkInService.obtenerPorId(id));
    }

    @PostMapping("/check-in")
    public ResponseEntity<Estancia> realizarCheckIn(
            @Valid @RequestBody CheckInRequestDTO request,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return new ResponseEntity<>(checkInService.realizarCheckIn(request, usuario), HttpStatus.CREATED);
    }

    @PostMapping("/check-out")
    public ResponseEntity<Estancia> realizarCheckOut(
            @Valid @RequestBody CheckOutRequestDTO request,
            @RequestParam(defaultValue = "RECEPCION") String usuario
    ) {
        return ResponseEntity.ok(checkInService.realizarCheckOut(request, usuario));
    }
}
