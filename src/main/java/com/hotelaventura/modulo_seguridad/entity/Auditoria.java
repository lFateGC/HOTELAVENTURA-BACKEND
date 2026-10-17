package com.hotelaventura.modulo_seguridad.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false, length = 30)
    private String accion; // LOGIN, CREAR_RESERVA, CHECK_IN, CHECK_OUT, COBRO_POS, CAMBIO_ESTADO

    @Column(nullable = false, length = 50)
    private String entidad; // Habitacion, Reserva, Estancia, Folio, etc.

    @Column(name = "id_entidad")
    private Long idEntidad;

    @Column(length = 500)
    private String detalle;

    @Column(length = 45)
    private String ip;

    @Builder.Default
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();
}
