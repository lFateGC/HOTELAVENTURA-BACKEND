-- ===================================================================
-- HOTEL AVENTURA (PMS) — SCRIPT DE CREACIÓN Y POBLADO DE BASE DE DATOS
-- Gestor: PostgreSQL (Compatible con Supabase SQL Editor)
-- Versión: 2.0 Modular (Java 21 LTS + Spring Boot 4)
-- ===================================================================

-- 1. TABLA: ROLES DE USUARIO
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(30) UNIQUE NOT NULL,
    descripcion VARCHAR(150)
);

-- 2. TABLA: USUARIOS DEL SISTEMA
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Hash BCrypt
    email VARCHAR(100) UNIQUE NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    telefono VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    ultimo_acceso TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABLA INTERMEDIA: USUARIO_ROLES (M:N)
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_rol_rol FOREIGN KEY (rol_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- 4. TABLA: BITÁCORA DE AUDITORÍA
CREATE TABLE IF NOT EXISTS auditoria (
    id BIGSERIAL PRIMARY KEY,
    usuario VARCHAR(60) NOT NULL,
    accion VARCHAR(60) NOT NULL,
    entidad VARCHAR(60) NOT NULL,
    id_entidad BIGINT,
    detalle VARCHAR(500),
    ip VARCHAR(45),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. TABLA: PISOS / NIVELES DEL HOTEL
CREATE TABLE IF NOT EXISTS pisos (
    id BIGSERIAL PRIMARY KEY,
    numero INT UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150)
);

-- 6. TABLA: TIPOS DE HABITACIÓN
CREATE TABLE IF NOT EXISTS tipos_habitacion (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(60) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    capacidad_adultos INT NOT NULL DEFAULT 2,
    capacidad_ninos INT NOT NULL DEFAULT 1,
    precio_base NUMERIC(12, 2) NOT NULL,
    precio_day_use NUMERIC(12, 2),
    imagen_url VARCHAR(500)
);

-- 7. TABLA: HABITACIONES FÍSICAS
CREATE TABLE IF NOT EXISTS habitaciones (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(10) UNIQUE NOT NULL,
    piso_id BIGINT NOT NULL,
    tipo_habitacion_id BIGINT NOT NULL,
    estado_operativo VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE', -- DISPONIBLE, OCUPADA, RESERVADA, FUERA_DE_SERVICIO
    estado_limpieza VARCHAR(30) NOT NULL DEFAULT 'LIMPIA',      -- LIMPIA, SUCIA, EN_LIMPIEZA, EN_INSPECCION
    tarifa_personalizada NUMERIC(12, 2),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    observaciones VARCHAR(255),
    CONSTRAINT fk_habitacion_piso FOREIGN KEY (piso_id) REFERENCES pisos (id),
    CONSTRAINT fk_habitacion_tipo FOREIGN KEY (tipo_habitacion_id) REFERENCES tipos_habitacion (id)
);

-- 8. TABLA: HUÉSPEDES (FICHA OFICIAL MINCETUR)
CREATE TABLE IF NOT EXISTS huespedes (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    tipo_documento VARCHAR(30) NOT NULL, -- DNI, PASAPORTE, CARNET_EXTRANJERIA, RUC, OTRO
    numero_documento VARCHAR(30) UNIQUE NOT NULL,
    fecha_nacimiento DATE,
    sexo VARCHAR(20),
    nacionalidad VARCHAR(60) NOT NULL DEFAULT 'Peruana',
    lugar_residencia VARCHAR(120),
    email VARCHAR(100),
    telefono VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. TABLA: RESERVAS
CREATE TABLE IF NOT EXISTS reservas (
    id BIGSERIAL PRIMARY KEY,
    codigo_reserva VARCHAR(20) UNIQUE NOT NULL,
    huesped_id BIGINT NOT NULL,
    tipo_habitacion_id BIGINT,
    habitacion_asignada_id BIGINT,
    fecha_llegada DATE NOT NULL,
    fecha_salida DATE NOT NULL,
    numero_adultos INT NOT NULL DEFAULT 1,
    numero_ninos INT NOT NULL DEFAULT 0,
    total_estimado NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'CONFIRMADA', -- PENDIENTE, CONFIRMADA, EN_CURSO, COMPLETADA, CANCELADA, NO_SHOW
    notas VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reserva_huesped FOREIGN KEY (huesped_id) REFERENCES huespedes (id),
    CONSTRAINT fk_reserva_tipo FOREIGN KEY (tipo_habitacion_id) REFERENCES tipos_habitacion (id),
    CONSTRAINT fk_reserva_habitacion FOREIGN KEY (habitacion_asignada_id) REFERENCES habitaciones (id)
);

-- 10. TABLA: ESTANCIAS (CHECK-IN / RECEPCIÓN)
CREATE TABLE IF NOT EXISTS estancias (
    id BIGSERIAL PRIMARY KEY,
    reserva_id BIGINT UNIQUE,
    habitacion_id BIGINT NOT NULL,
    check_in_real TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    check_out_previsto TIMESTAMP NOT NULL,
    check_out_real TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVA', -- ACTIVA, FINALIZADA, CANCELADA
    modalidad VARCHAR(30) NOT NULL DEFAULT 'PERNOCTE', -- PERNOCTE, DAY_USE
    observaciones VARCHAR(255),
    CONSTRAINT fk_estancia_reserva FOREIGN KEY (reserva_id) REFERENCES reservas (id),
    CONSTRAINT fk_estancia_habitacion FOREIGN KEY (habitacion_id) REFERENCES habitaciones (id)
);

-- 11. TABLA: REGISTRO HUÉSPED POR ESTANCIA
CREATE TABLE IF NOT EXISTS registro_huespedes (
    id BIGSERIAL PRIMARY KEY,
    estancia_id BIGINT NOT NULL,
    huesped_id BIGINT NOT NULL,
    motivo_viaje VARCHAR(40) NOT NULL DEFAULT 'TURISMO', -- TURISMO, NEGOCIOS, CONVENCION, SALUD, OTRO
    firma_registro VARCHAR(255),
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_salida TIMESTAMP,
    CONSTRAINT fk_reg_estancia FOREIGN KEY (estancia_id) REFERENCES estancias (id),
    CONSTRAINT fk_reg_huesped FOREIGN KEY (huesped_id) REFERENCES huespedes (id)
);

-- 12. TABLA: HOUSEKEEPING / TAREAS DE LIMPIEZA
CREATE TABLE IF NOT EXISTS tareas_limpieza (
    id BIGSERIAL PRIMARY KEY,
    habitacion_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL, -- SALIDA, ESTADIA, RETOQUE, PROFUNDA
    asignada_a VARCHAR(80),
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE, EN_PROCESO, FINALIZADA, INSPECCIONADA
    hora_inicio TIMESTAMP,
    hora_fin TIMESTAMP,
    notas VARCHAR(255),
    CONSTRAINT fk_limpieza_habitacion FOREIGN KEY (habitacion_id) REFERENCES habitaciones (id)
);

-- 13. TABLA: INCIDENCIAS Y AVERÍAS DE MANTENIMIENTO
CREATE TABLE IF NOT EXISTS incidencias_averias (
    id BIGSERIAL PRIMARY KEY,
    habitacion_id BIGINT NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    prioridad VARCHAR(20) NOT NULL DEFAULT 'MEDIA', -- BAJA, MEDIA, ALTA, URGENTE
    estado VARCHAR(20) NOT NULL DEFAULT 'REPORTADA', -- REPORTADA, EN_REVISION, EN_REPARACION, RESUELTA, CANCELADA
    reportada_por VARCHAR(80) NOT NULL,
    asignada_a VARCHAR(80),
    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_solucion TIMESTAMP,
    solucion_aplicada VARCHAR(500),
    foto_evidencia_url VARCHAR(500),
    CONSTRAINT fk_incidencia_habitacion FOREIGN KEY (habitacion_id) REFERENCES habitaciones (id)
);

-- 14. TABLA: CATÁLOGO DE PRODUCTOS Y SERVICIOS
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    categoria VARCHAR(30) NOT NULL, -- BEBIDAS, SNACKS, RESTAURANTE, MINIBAR, LAVANDERIA, SOUVENIR, OTRO
    precio_venta NUMERIC(12, 2) NOT NULL,
    costo_unitario NUMERIC(12, 2),
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    controla_stock BOOLEAN NOT NULL DEFAULT TRUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    imagen_url VARCHAR(500)
);

-- 15. TABLA: TURNOS DE CAJA
CREATE TABLE IF NOT EXISTS turnos_caja (
    id BIGSERIAL PRIMARY KEY,
    cajero VARCHAR(80) NOT NULL,
    fecha_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP,
    monto_inicial NUMERIC(12, 2) NOT NULL,
    monto_final_contado NUMERIC(12, 2),
    monto_esperado NUMERIC(12, 2),
    diferencia NUMERIC(12, 2),
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA', -- ABIERTA, CERRADA, AUDITADA
    observaciones VARCHAR(255)
);

-- 16. TABLA: FOLIOS (CUENTAS DE HUÉSPED)
CREATE TABLE IF NOT EXISTS folios (
    id BIGSERIAL PRIMARY KEY,
    estancia_id BIGINT UNIQUE,
    reserva_id BIGINT,
    total_cargos NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    total_abonos NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    saldo NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTO', -- ABIERTO, CERRADO, ANULADO
    fecha_apertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP,
    CONSTRAINT fk_folio_estancia FOREIGN KEY (estancia_id) REFERENCES estancias (id),
    CONSTRAINT fk_folio_reserva FOREIGN KEY (reserva_id) REFERENCES reservas (id)
);

-- 17. TABLA: CARGOS AL FOLIO
CREATE TABLE IF NOT EXISTS cargos (
    id BIGSERIAL PRIMARY KEY,
    folio_id BIGINT NOT NULL,
    concepto VARCHAR(120) NOT NULL,
    categoria VARCHAR(30) NOT NULL, -- ALOJAMIENTO, RESTAURANTE, MINIBAR, LAVANDERIA, PENALIDAD, IMPUESTO, OTRO
    monto NUMERIC(12, 2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cargo_folio FOREIGN KEY (folio_id) REFERENCES folios (id) ON DELETE CASCADE
);

-- 18. TABLA: PAGOS Y ABONOS AL FOLIO
CREATE TABLE IF NOT EXISTS pagos (
    id BIGSERIAL PRIMARY KEY,
    folio_id BIGINT NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL, -- EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, YAPE_PLIN
    monto NUMERIC(12, 2) NOT NULL,
    referencia VARCHAR(80),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pago_folio FOREIGN KEY (folio_id) REFERENCES folios (id) ON DELETE CASCADE
);

-- 19. TABLA: VENTAS DE MOSTRADOR / POS
CREATE TABLE IF NOT EXISTS ventas_pos (
    id BIGSERIAL PRIMARY KEY,
    cajero VARCHAR(80) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(12, 2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    folio_id BIGINT,
    habitacion_numero VARCHAR(10),
    cliente_nombre VARCHAR(120),
    CONSTRAINT fk_venta_folio FOREIGN KEY (folio_id) REFERENCES folios (id)
);

-- 20. TABLA: DETALLE DE VENTA POS
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario NUMERIC(12, 2) NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL,
    CONSTRAINT fk_det_venta FOREIGN KEY (venta_id) REFERENCES ventas_pos (id) ON DELETE CASCADE,
    CONSTRAINT fk_det_prod FOREIGN KEY (producto_id) REFERENCES productos (id)
);

-- ===================================================================
-- POBLADO INICIAL DE DATOS (SEED DATA)
-- ===================================================================

-- Roles del Hotel
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador general del sistema con acceso total'),
('GERENTE', 'Gerencia con acceso a reportes, auditoria y supervision'),
('RECEPCION', 'Recepcionistas: reservas, check-in, check-out y folios'),
('HOUSEKEEPING', 'Personal de limpieza y ama de llaves'),
('MANTENIMIENTO', 'Tecnicos de soporte de averias e incidencias'),
('CAJERO', 'Cajeros de punto de venta y restaurante'),
('CLIENTE', 'Clientes registrados desde el portal web')
ON CONFLICT (nombre) DO NOTHING;

-- Usuario Administrador Inicial
-- Usuario: admin | Contraseña: password123 (Cifrado seguro con BCrypt)
INSERT INTO usuarios (username, password, email, nombre_completo, telefono, activo) VALUES
('admin', '$2a$10$wNnIe4G.l16kI7/P8u2kqupM5e3wZ6iR1Z9qF4wSjW3jV5vWzX9.m', 'admin@hotelaventura.com', 'Administrador Principal', '999888777', true)
ON CONFLICT (username) DO NOTHING;

-- Asignar rol ADMIN al usuario admin
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'admin' AND r.nombre = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Pisos
INSERT INTO pisos (numero, nombre, descripcion) VALUES
(1, 'Piso 1 - Recepción y Lobby', 'Área de recepción, cafetería y habitaciones estándar'),
(2, 'Piso 2 - Habitaciones Ejecutivas', 'Habitaciones dobles y matrimoniales con vista al jardín'),
(3, 'Piso 3 - Suites Panorámicas', 'Suites de lujo con jacuzzi y terraza privada')
ON CONFLICT (numero) DO NOTHING;

-- Tipos de Habitación
INSERT INTO tipos_habitacion (nombre, descripcion, capacidad_adultos, capacidad_ninos, precio_base, precio_day_use, imagen_url) VALUES
('Simple Estándar', 'Habitación cómoda para 1 persona, cama de 1.5 plazas, baño privado y Wi-Fi', 1, 0, 80.00, 50.00, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=800&q=80'),
('Matrimonial Confort', 'Cama Queen Size, TV 50", minibar, ideal para parejas', 2, 1, 130.00, 80.00, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=800&q=80'),
('Doble Twin', 'Dos camas de 2 plazas, escritorio de trabajo, aire acondicionado', 2, 2, 160.00, 100.00, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=800&q=80'),
('Suite Ejecutiva Premium', 'Cama King Size, sala de estar, jacuzzi, vista panorámica y cafetera', 2, 2, 250.00, 150.00, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80')
ON CONFLICT (nombre) DO NOTHING;

-- Habitaciones Físicas
INSERT INTO habitaciones (numero, piso_id, tipo_habitacion_id, estado_operativo, estado_limpieza, tarifa_personalizada, activa) VALUES
('101', 1, 1, 'DISPONIBLE', 'LIMPIA', NULL, true),
('102', 1, 1, 'DISPONIBLE', 'LIMPIA', NULL, true),
('103', 1, 2, 'DISPONIBLE', 'LIMPIA', NULL, true),
('201', 2, 2, 'DISPONIBLE', 'LIMPIA', NULL, true),
('202', 2, 3, 'DISPONIBLE', 'LIMPIA', NULL, true),
('203', 2, 3, 'DISPONIBLE', 'LIMPIA', NULL, true),
('301', 3, 4, 'DISPONIBLE', 'LIMPIA', NULL, true),
('302', 3, 4, 'DISPONIBLE', 'LIMPIA', NULL, true)
ON CONFLICT (numero) DO NOTHING;

-- Productos del POS y Minibar
INSERT INTO productos (nombre, categoria, precio_venta, costo_unitario, stock_actual, stock_minimo, controla_stock, activo, imagen_url) VALUES
('Agua Mineral San Mateo 600ml', 'BEBIDAS', 5.00, 1.80, 50, 10, true, true, 'https://images.unsplash.com/photo-1559839914-1b3464713e32?auto=format&fit=crop&w=400&q=80'),
('Cerveza Cusqueña Dorada 330ml', 'BEBIDAS', 10.00, 4.50, 40, 12, true, true, 'https://images.unsplash.com/photo-1608270199923-d34346e01a52?auto=format&fit=crop&w=400&q=80'),
('Sandwich Club House', 'RESTAURANTE', 25.00, 10.00, 20, 5, false, true, 'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=400&q=80'),
('Desayuno Americano Completo', 'RESTAURANTE', 20.00, 8.00, 30, 5, false, true, 'https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?auto=format&fit=crop&w=400&q=80'),
('Papas Lays Clásicas', 'SNACKS', 6.00, 2.50, 35, 10, true, true, 'https://images.unsplash.com/photo-1566478989037-eec170784d0b?auto=format&fit=crop&w=400&q=80'),
('Servicio Lavandería por Kilo', 'LAVANDERIA', 15.00, 4.00, 100, 10, false, true, NULL)
ON CONFLICT (nombre) DO NOTHING;
