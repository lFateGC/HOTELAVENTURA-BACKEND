package com.hotelaventura.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sistema_colecciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SistemaColeccion {

    @Id
    @Column(length = 60)
    private String clave;

    @Column(name = "contenido_json", columnDefinition = "TEXT", nullable = false)
    private String contenidoJson;

    @Builder.Default
    @Column(name = "actualizado_el", nullable = false)
    private LocalDateTime actualizadoEl = LocalDateTime.now();
}
