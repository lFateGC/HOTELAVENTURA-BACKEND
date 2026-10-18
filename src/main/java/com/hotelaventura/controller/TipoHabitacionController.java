package com.hotelaventura.modulo_habitaciones_reservas.controller;

import com.hotelaventura.modulo_habitaciones_reservas.dto.TipoHabitacionDTO;
import com.hotelaventura.modulo_habitaciones_reservas.entity.TipoHabitacion;
import com.hotelaventura.modulo_habitaciones_reservas.repository.TipoHabitacionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-habitacion")
@RequiredArgsConstructor
public class TipoHabitacionController {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    @GetMapping
    public ResponseEntity<List<TipoHabitacionDTO>> listar() {
        List<TipoHabitacionDTO> list = tipoHabitacionRepository.findAll().stream()
                .map(t -> TipoHabitacionDTO.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .descripcion(t.getDescripcion())
                        .capacidadAdultos(t.getCapacidadAdultos())
                        .capacidadNinos(t.getCapacidadNinos())
                        .precioBase(t.getPrecioBase())
                        .precioDayUse(t.getPrecioDayUse())
                        .imagenUrl(t.getImagenUrl())
                        .build())
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<TipoHabitacionDTO> crear(@Valid @RequestBody TipoHabitacionDTO dto) {
        TipoHabitacion tipo = TipoHabitacion.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .capacidadAdultos(dto.getCapacidadAdultos())
                .capacidadNinos(dto.getCapacidadNinos())
                .precioBase(dto.getPrecioBase())
                .precioDayUse(dto.getPrecioDayUse())
                .imagenUrl(dto.getImagenUrl())
                .build();
        TipoHabitacion guardado = tipoHabitacionRepository.save(tipo);
        dto.setId(guardado.getId());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoHabitacionDTO> actualizar(@PathVariable Long id, @RequestBody TipoHabitacionDTO dto) {
        return tipoHabitacionRepository.findById(id).map(tipo -> {
            if (dto.getNombre() != null) tipo.setNombre(dto.getNombre());
            if (dto.getDescripcion() != null) tipo.setDescripcion(dto.getDescripcion());
            if (dto.getCapacidadAdultos() != null) tipo.setCapacidadAdultos(dto.getCapacidadAdultos());
            if (dto.getCapacidadNinos() != null) tipo.setCapacidadNinos(dto.getCapacidadNinos());
            if (dto.getPrecioBase() != null) tipo.setPrecioBase(dto.getPrecioBase());
            if (dto.getPrecioDayUse() != null) tipo.setPrecioDayUse(dto.getPrecioDayUse());
            if (dto.getImagenUrl() != null) tipo.setImagenUrl(dto.getImagenUrl());
            TipoHabitacion actualizado = tipoHabitacionRepository.save(tipo);
            dto.setId(actualizado.getId());
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }
}
