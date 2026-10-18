package com.hotelaventura.modulo_habitaciones_reservas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tipos_habitacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre; // Simple, Matrimonial, Doble Twin, Suite Ejecutiva

    @Column(length = 255)
    private String descripcion;

    @Column(name = "capacidad_adultos", nullable = false)
    private Integer capacidadAdultos;

    @Column(name = "capacidad_ninos", nullable = false)
    private Integer capacidadNinos;

    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "precio_day_use", precision = 12, scale = 2)
    private BigDecimal precioDayUse;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
}
