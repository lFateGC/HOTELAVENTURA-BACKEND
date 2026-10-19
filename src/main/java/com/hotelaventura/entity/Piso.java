package com.hotelaventura.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pisos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Piso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false, length = 50)
    private String nombre; // Ej. "Piso 1 - Recepcion", "Piso 2", "Piso 3"
}
