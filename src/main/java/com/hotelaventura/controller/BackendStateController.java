package com.hotelaventura.controller;

import com.hotelaventura.entity.*;
import com.hotelaventura.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
public class BackendStateController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PisoRepository pisoRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final HuespedRepository huespedRepository;
    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final SistemaColeccionRepository sistemaColeccionRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @GetMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<String> obtenerEstadoDesdePostgres() {
        Map<String, Object> state = new LinkedHashMap<>();

        // 1. Usuarios desde tabla `usuarios`
        List<Map<String, Object>> usersList = new ArrayList<>();
        for (Usuario u : usuarioRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("nombre", u.getNombreCompleto());
            m.put("usuario", u.getUsername());
            m.put("passwordHash", mapDefaultPasswordForUser(u.getUsername()));
            m.put("rol", mapRolToFrontend(u.getRoles()));
            m.put("estado", Boolean.TRUE.equals(u.getActivo()) ? "Activo" : "Inactivo");
            usersList.add(m);
        }
        state.put("users", usersList);

        // 2. Categorías desde tabla `tipos_habitacion`
        List<Map<String, Object>> categoriasList = new ArrayList<>();
        for (TipoHabitacion t : tipoHabitacionRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", t.getId());
            m.put("nombre", t.getNombre());
            int cap = (t.getCapacidadAdultos() != null ? t.getCapacidadAdultos() : 1)
                    + (t.getCapacidadNinos() != null ? t.getCapacidadNinos() : 0);
            m.put("capacidad", Math.max(1, cap));
            m.put("tarifaBase", t.getPrecioBase() != null ? t.getPrecioBase().doubleValue() : 140.0);
            m.put("descripcion", t.getDescripcion() != null ? t.getDescripcion() : "");
            m.put("estado", "Activa");
            categoriasList.add(m);
        }
        state.put("categorias", categoriasList);

        // 3. Habitaciones desde tabla `habitaciones`
        List<Map<String, Object>> habitacionesList = new ArrayList<>();
        for (Habitacion h : habitacionRepository.findAllByOrderByNumeroAsc()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("numero", h.getNumero());
            m.put("piso", h.getPiso() != null ? h.getPiso().getNumero() : 1);
            m.put("categoriaId", h.getTipoHabitacion() != null ? h.getTipoHabitacion().getId() : 1L);
            m.put("estado", mapEstadoHabitacionToFrontend(h));
            habitacionesList.add(m);
        }
        state.put("habitaciones", habitacionesList);

        // 4. Clientes / Huéspedes desde tabla `huespedes`
        List<Map<String, Object>> clientesList = new ArrayList<>();
        for (Huesped hu : huespedRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", hu.getId());
            boolean esEmpresa = hu.getTipoDocumento() == TipoDocumento.RUC;
            m.put("tipo", esEmpresa ? "Empresa" : "Persona Natural");
            m.put("tipoDoc", mapTipoDocToFrontend(hu.getTipoDocumento()));
            m.put("documento", hu.getNumeroDocumento());
            String nombreCompleto = ((hu.getNombres() != null ? hu.getNombres() : "") + " "
                    + (hu.getApellidos() != null && !"-".equals(hu.getApellidos()) ? hu.getApellidos() : "")).trim();
            m.put("nombre", nombreCompleto);
            m.put("telefono", hu.getTelefono() != null ? hu.getTelefono() : "");
            m.put("email", hu.getEmail() != null ? hu.getEmail() : "");
            m.put("direccion", hu.getResidencia() != null ? hu.getResidencia() : "");
            m.put("estado", Boolean.TRUE.equals(hu.getActivo()) ? "Activo" : "Inactivo");
            clientesList.add(m);
        }
        state.put("clientes", clientesList);

        // 5. Reservas desde tabla `reservas`
        List<Huesped> allHuespedes = huespedRepository.findAll();
        List<Map<String, Object>> reservasList = new ArrayList<>();
        for (Reserva r : reservaRepository.findAllByOrderByFechaCreacionDesc()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("codigo", r.getCodigo());
            Long clienteId = allHuespedes.stream()
                    .filter(h -> h.getNumeroDocumento().equalsIgnoreCase(r.getDocumentoTitular()))
                    .map(Huesped::getId)
                    .findFirst()
                    .orElse(0L);
            m.put("clienteId", clienteId);
            String habNum = r.getHabitacionAsignada() != null ? r.getHabitacionAsignada().getNumero() : "101";
            m.put("habitacionNumero", habNum);
            m.put("entrada", r.getFechaLlegada() != null ? r.getFechaLlegada().toString() : LocalDate.now().toString());
            m.put("salida", r.getFechaSalida() != null ? r.getFechaSalida().toString() : LocalDate.now().plusDays(1).toString());
            int huesp = (r.getAdultos() != null ? r.getAdultos() : 1) + (r.getNinos() != null ? r.getNinos() : 0);
            m.put("huespedes", Math.max(1, huesp));
            double tarifaBase = r.getTipoHabitacion() != null && r.getTipoHabitacion().getPrecioBase() != null
                    ? r.getTipoHabitacion().getPrecioBase().doubleValue() : 140.0;
            m.put("tarifaBasePorNoche", tarifaBase);
            m.put("ajusteTemporada", 0);
            m.put("totalFijo", r.getTotalEstimado() != null ? r.getTotalEstimado().doubleValue() : tarifaBase);
            m.put("estado", mapEstadoReservaToFrontend(r.getEstado()));
            m.put("adelanto", r.getAdelantoDeposito() != null ? r.getAdelantoDeposito().doubleValue() : 0.0);
            m.put("adelantoRetenido", false);
            m.put("observaciones", r.getMotivoCancelacion() != null ? r.getMotivoCancelacion() : "");
            m.put("creadoEl", r.getFechaCreacion() != null ? r.getFechaCreacion().format(DT_FMT) : LocalDateTime.now().format(DT_FMT));
            m.put("creadoPor", "admin");
            m.put("acompanantes", Collections.emptyList());
            if (r.getMotivoCancelacion() != null) {
                m.put("motivoCancelacion", r.getMotivoCancelacion());
            }
            reservasList.add(m);
        }
        state.put("reservas", reservasList);

        // 6. Productos y Servicios desde tabla `productos`
        List<Map<String, Object>> productosList = new ArrayList<>();
        List<Map<String, Object>> serviciosList = new ArrayList<>();
        for (Producto p : productoRepository.findAllByOrderByNombreAsc()) {
            String catStr = mapCategoriaProductoToFrontend(p.getCategoria());
            if (Boolean.TRUE.equals(p.getControlaStock())) {
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("id", p.getId());
                pm.put("nombre", p.getNombre());
                pm.put("categoria", catStr);
                pm.put("precio", p.getPrecioVenta() != null ? p.getPrecioVenta().doubleValue() : 0.0);
                pm.put("stock", p.getStockActual() != null ? p.getStockActual() : 0);
                pm.put("stockMinimo", p.getStockMinimo() != null ? p.getStockMinimo() : 5);
                productosList.add(pm);
            }
            Map<String, Object> sm = new LinkedHashMap<>();
            sm.put("id", p.getId());
            sm.put("nombre", p.getNombre());
            sm.put("categoria", catStr);
            sm.put("precio", p.getPrecioVenta() != null ? p.getPrecioVenta().doubleValue() : 0.0);
            sm.put("estado", Boolean.TRUE.equals(p.getActivo()) ? "Activo" : "Inactivo");
            sm.put("productoId", Boolean.TRUE.equals(p.getControlaStock()) ? p.getId() : null);
            serviciosList.add(sm);
        }
        state.put("productos", productosList);
        state.put("servicios", serviciosList);

        // 7. Incidencias desde tabla `incidencias_averias`
        List<Map<String, Object>> incidenciasList = new ArrayList<>();
        for (Incidencia inc : incidenciaRepository.findAll()) {
            Map<String, Object> im = new LinkedHashMap<>();
            im.put("id", inc.getId());
            im.put("habitacionNumero", inc.getHabitacion() != null ? inc.getHabitacion().getNumero() : "101");
            im.put("categoria", inc.getTitulo() != null ? inc.getTitulo() : "General");
            im.put("descripcion", inc.getDescripcion() != null ? inc.getDescripcion() : "");
            im.put("fecha", inc.getFechaReporte() != null ? inc.getFechaReporte().format(DT_FMT) : LocalDateTime.now().format(DT_FMT));
            im.put("estado", inc.getEstado() == EstadoIncidencia.RESUELTA ? "Resuelta" : "Abierta");
            im.put("usuario", inc.getReportadaPor() != null ? inc.getReportadaPor() : "recepcion1");
            if (inc.getSolucionAplicada() != null) {
                im.put("solucion", inc.getSolucionAplicada());
            }
            incidenciasList.add(im);
        }
        state.put("incidencias", incidenciasList);

        // 8. Auditoría desde tabla `auditoria`
        List<Map<String, Object>> auditoriaList = new ArrayList<>();
        List<Auditoria> auds = auditoriaRepository.findAll();
        auds.sort((a, b) -> b.getFechaHora().compareTo(a.getFechaHora()));
        for (int i = 0; i < Math.min(auds.size(), 100); i++) {
            Auditoria a = auds.get(i);
            Map<String, Object> am = new LinkedHashMap<>();
            am.put("id", a.getId());
            am.put("fecha", a.getFechaHora() != null ? a.getFechaHora().format(DT_FMT) : LocalDateTime.now().format(DT_FMT));
            am.put("modulo", a.getEntidad() != null ? a.getEntidad() : "Sistema");
            am.put("usuario", a.getUsuario() != null ? a.getUsuario() : "sistema");
            am.put("rol", "Personal");
            am.put("accion", a.getAccion() != null ? a.getAccion() : "Operación");
            am.put("detalle", a.getDetalle() != null ? a.getDetalle() : "");
            auditoriaList.add(am);
        }
        state.put("auditoria", auditoriaList);

        // 9. Colecciones operativas complementarias desde `sistema_colecciones`
        String extrasJson = sistemaColeccionRepository.findById("OPERACIONES_EXTRAS")
                .map(SistemaColeccion::getContenidoJson)
                .orElse("{}");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("exists", true);
        response.put("version", System.currentTimeMillis());
        response.put("state", state);
        response.put("extrasJson", extrasJson);
        return ResponseEntity.ok(toJson(response));
    }

    @PutMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> guardarEstadoEnPostgres(@RequestBody String rawPayload) {
        Map<String, Object> payload = JsonParserFactory.getJsonParser().parseMap(rawPayload);
        Map<String, Object> state = (Map<String, Object>) payload.get("state");
        String extrasJson = (String) payload.get("extrasJson");

        if (extrasJson != null) {
            SistemaColeccion col = sistemaColeccionRepository.findById("OPERACIONES_EXTRAS")
                    .orElseGet(() -> SistemaColeccion.builder().clave("OPERACIONES_EXTRAS").build());
            col.setContenidoJson(extrasJson);
            col.setActualizadoEl(LocalDateTime.now());
            sistemaColeccionRepository.save(col);
        }

        if (state != null) {
            // 1. Sincronizar Categorías (`tipos_habitacion`)
            List<Map<String, Object>> categorias = (List<Map<String, Object>>) state.get("categorias");
            if (categorias != null) {
                for (Map<String, Object> c : categorias) {
                    String nombre = String.valueOf(c.getOrDefault("nombre", "Estándar")).trim();
                    int capacidad = toInt(c.get("capacidad"), 2);
                    BigDecimal tarifa = toBigDecimal(c.get("tarifaBase"), "140.00");
                    String desc = String.valueOf(c.getOrDefault("descripcion", ""));

                    TipoHabitacion tipo = tipoHabitacionRepository.findAll().stream()
                            .filter(t -> t.getNombre().equalsIgnoreCase(nombre) || t.getId().equals(toLong(c.get("id"))))
                            .findFirst()
                            .orElseGet(TipoHabitacion::new);

                    tipo.setNombre(nombre);
                    tipo.setDescripcion(desc);
                    tipo.setCapacidadAdultos(Math.max(1, capacidad));
                    if (tipo.getCapacidadNinos() == null) tipo.setCapacidadNinos(0);
                    tipo.setPrecioBase(tarifa);
                    if (tipo.getPrecioDayUse() == null) tipo.setPrecioDayUse(tarifa.multiply(new BigDecimal("0.60")));
                    tipoHabitacionRepository.save(tipo);
                }
            }

            // 2. Sincronizar Habitaciones (`habitaciones`)
            List<Map<String, Object>> habitaciones = (List<Map<String, Object>>) state.get("habitaciones");
            if (habitaciones != null) {
                List<TipoHabitacion> tipos = tipoHabitacionRepository.findAll();
                TipoHabitacion defaultTipo = tipos.isEmpty() ? null : tipos.get(0);

                for (Map<String, Object> h : habitaciones) {
                    String numero = String.valueOf(h.getOrDefault("numero", "")).trim();
                    if (numero.isEmpty()) continue;
                    int pisoNum = toInt(h.get("piso"), 1);
                    Long catId = toLong(h.get("categoriaId"));
                    String estadoFront = String.valueOf(h.getOrDefault("estado", "Disponible"));

                    Piso piso = pisoRepository.findByNumero(pisoNum)
                            .orElseGet(() -> pisoRepository.save(Piso.builder().numero(pisoNum).nombre("Piso " + pisoNum).build()));

                    TipoHabitacion tipo = tipos.stream()
                            .filter(t -> t.getId().equals(catId))
                            .findFirst()
                            .orElse(defaultTipo);

                    if (tipo == null) continue;

                    Habitacion hab = habitacionRepository.findByNumero(numero)
                            .orElseGet(() -> Habitacion.builder().numero(numero).activa(true).build());
                    hab.setPiso(piso);
                    hab.setTipoHabitacion(tipo);
                    hab.setObservaciones(estadoFront);

                    if ("Ocupada".equalsIgnoreCase(estadoFront)) {
                        hab.setEstadoOperativo(EstadoOperativo.OCUPADA);
                        hab.setEstadoLimpieza(EstadoLimpieza.LIMPIA);
                    } else if ("Mantenimiento".equalsIgnoreCase(estadoFront)) {
                        hab.setEstadoOperativo(EstadoOperativo.FUERA_DE_SERVICIO);
                    } else if ("En Limpieza".equalsIgnoreCase(estadoFront)) {
                        hab.setEstadoOperativo(EstadoOperativo.DISPONIBLE);
                        hab.setEstadoLimpieza(EstadoLimpieza.SUCIA);
                    } else {
                        hab.setEstadoOperativo(EstadoOperativo.DISPONIBLE);
                        hab.setEstadoLimpieza(EstadoLimpieza.LIMPIA);
                    }
                    habitacionRepository.save(hab);
                }
            }

            // 3. Sincronizar Clientes (`huespedes`)
            List<Map<String, Object>> clientes = (List<Map<String, Object>>) state.get("clientes");
            if (clientes != null) {
                for (Map<String, Object> cl : clientes) {
                    String doc = String.valueOf(cl.getOrDefault("documento", "")).trim();
                    if (doc.isEmpty()) continue;
                    String nombre = String.valueOf(cl.getOrDefault("nombre", "Huésped")).trim();
                    String tipoDocStr = String.valueOf(cl.getOrDefault("tipoDoc", "DNI"));
                    String telefono = String.valueOf(cl.getOrDefault("telefono", ""));
                    String email = cl.get("email") != null ? String.valueOf(cl.get("email")).trim() : null;
                    if (email != null && email.isEmpty()) email = null;
                    String direccion = cl.get("direccion") != null ? String.valueOf(cl.get("direccion")) : "";
                    boolean activo = !"Inactivo".equalsIgnoreCase(String.valueOf(cl.getOrDefault("estado", "Activo")));

                    Huesped huesped = huespedRepository.findByNumeroDocumento(doc)
                            .orElseGet(() -> Huesped.builder().numeroDocumento(doc).nacionalidad("Peruana").build());

                    huesped.setNombres(nombre);
                    huesped.setApellidos("-");
                    huesped.setTipoDocumento(mapTipoDocFromFrontend(tipoDocStr));
                    huesped.setTelefono(telefono);
                    huesped.setEmail(email);
                    huesped.setResidencia(direccion);
                    huesped.setActivo(activo);
                    huespedRepository.save(huesped);
                }
            }

            // 4. Sincronizar Reservas (`reservas`)
            List<Map<String, Object>> reservas = (List<Map<String, Object>>) state.get("reservas");
            if (reservas != null) {
                List<Huesped> allHuespedes = huespedRepository.findAll();
                List<Habitacion> allHabs = habitacionRepository.findAll();
                List<TipoHabitacion> allTipos = tipoHabitacionRepository.findAll();

                for (Map<String, Object> r : reservas) {
                    String codigo = String.valueOf(r.getOrDefault("codigo", "")).trim();
                    if (codigo.isEmpty()) continue;

                    Long clienteId = toLong(r.get("clienteId"));
                    String habNum = String.valueOf(r.getOrDefault("habitacionNumero", "101"));

                    String nombreTitular = "Huésped Titular";
                    String docTitular = "00000000";
                    String telTitular = "";
                    String emailTitular = "";

                    if (clientes != null) {
                        for (Map<String, Object> cl : clientes) {
                            if (Objects.equals(toLong(cl.get("id")), clienteId)) {
                                nombreTitular = String.valueOf(cl.getOrDefault("nombre", nombreTitular));
                                docTitular = String.valueOf(cl.getOrDefault("documento", docTitular));
                                telTitular = String.valueOf(cl.getOrDefault("telefono", ""));
                                emailTitular = cl.get("email") != null ? String.valueOf(cl.get("email")) : "";
                                break;
                            }
                        }
                    }
                    if ("00000000".equals(docTitular)) {
                        for (Huesped h : allHuespedes) {
                            if (Objects.equals(h.getId(), clienteId)) {
                                nombreTitular = (h.getNombres() + ("-".equals(h.getApellidos()) ? "" : " " + h.getApellidos())).trim();
                                docTitular = h.getNumeroDocumento();
                                telTitular = h.getTelefono();
                                emailTitular = h.getEmail();
                                break;
                            }
                        }
                    }

                    Habitacion hab = allHabs.stream()
                            .filter(h -> h.getNumero().equalsIgnoreCase(habNum))
                            .findFirst()
                            .orElse(allHabs.isEmpty() ? null : allHabs.get(0));

                    TipoHabitacion tipo = hab != null && hab.getTipoHabitacion() != null
                            ? hab.getTipoHabitacion()
                            : (allTipos.isEmpty() ? null : allTipos.get(0));

                    if (tipo == null) continue;

                    LocalDate entrada = parseLocalDate(r.get("entrada"), LocalDate.now());
                    LocalDate salida = parseLocalDate(r.get("salida"), entrada.plusDays(1));
                    int huespedes = Math.max(1, toInt(r.get("huespedes"), 1));
                    BigDecimal totalFijo = toBigDecimal(r.get("totalFijo"), "140.00");
                    BigDecimal adelanto = toBigDecimal(r.get("adelanto"), "0.00");
                    String estadoStr = String.valueOf(r.getOrDefault("estado", "Confirmada"));
                    String obs = r.get("motivoCancelacion") != null
                            ? String.valueOf(r.get("motivoCancelacion"))
                            : (r.get("observaciones") != null ? String.valueOf(r.get("observaciones")) : null);

                    Reserva resEntity = reservaRepository.findByCodigo(codigo)
                            .orElseGet(() -> Reserva.builder().codigo(codigo).build());

                    resEntity.setNombreTitular(nombreTitular);
                    resEntity.setDocumentoTitular(docTitular);
                    resEntity.setTelefonoTitular(telTitular);
                    resEntity.setEmailTitular(emailTitular);
                    resEntity.setTipoHabitacion(tipo);
                    resEntity.setHabitacionAsignada(hab);
                    resEntity.setFechaLlegada(entrada);
                    resEntity.setFechaSalida(salida);
                    resEntity.setAdultos(huespedes);
                    resEntity.setNinos(0);
                    resEntity.setTotalEstimado(totalFijo);
                    resEntity.setAdelantoDeposito(adelanto);
                    resEntity.setEstado(mapEstadoReservaFromFrontend(estadoStr));
                    resEntity.setMotivoCancelacion(obs);
                    reservaRepository.save(resEntity);
                }
            }

            // 5. Sincronizar Productos (`productos`)
            List<Map<String, Object>> productos = (List<Map<String, Object>>) state.get("productos");
            if (productos != null) {
                for (Map<String, Object> p : productos) {
                    String nombre = String.valueOf(p.getOrDefault("nombre", "")).trim();
                    if (nombre.isEmpty()) continue;
                    String catStr = String.valueOf(p.getOrDefault("categoria", "MINIBAR"));
                    BigDecimal precio = toBigDecimal(p.get("precio"), "5.00");
                    int stock = toInt(p.get("stock"), 0);
                    int stockMin = toInt(p.get("stockMinimo"), 5);

                    Producto prod = productoRepository.findByNombre(nombre)
                            .orElseGet(() -> Producto.builder().nombre(nombre).controlaStock(true).activo(true).build());
                    prod.setCategoria(mapCategoriaProductoFromFrontend(catStr));
                    prod.setPrecioVenta(precio);
                    prod.setStockActual(stock);
                    prod.setStockMinimo(stockMin);
                    productoRepository.save(prod);
                }
            }

            // 6. Sincronizar Incidencias (`incidencias_averias`)
            List<Map<String, Object>> incidencias = (List<Map<String, Object>>) state.get("incidencias");
            if (incidencias != null) {
                List<Habitacion> allHabs = habitacionRepository.findAll();
                List<Incidencia> existingIncs = incidenciaRepository.findAll();

                for (Map<String, Object> inc : incidencias) {
                    String habNum = String.valueOf(inc.getOrDefault("habitacionNumero", "101"));
                    String cat = String.valueOf(inc.getOrDefault("categoria", "Mantenimiento"));
                    String desc = String.valueOf(inc.getOrDefault("descripcion", ""));
                    String estadoStr = String.valueOf(inc.getOrDefault("estado", "Abierta"));
                    String usuario = String.valueOf(inc.getOrDefault("usuario", "recepcion1"));
                    String solucion = inc.get("solucion") != null ? String.valueOf(inc.get("solucion")) : null;

                    Habitacion hab = allHabs.stream()
                            .filter(h -> h.getNumero().equalsIgnoreCase(habNum))
                            .findFirst()
                            .orElse(allHabs.isEmpty() ? null : allHabs.get(0));
                    if (hab == null) continue;

                    Incidencia match = existingIncs.stream()
                            .filter(e -> e.getHabitacion().getNumero().equalsIgnoreCase(habNum)
                                    && e.getDescripcion().equalsIgnoreCase(desc))
                            .findFirst()
                            .orElseGet(() -> Incidencia.builder()
                                    .habitacion(hab)
                                    .titulo(cat)
                                    .descripcion(desc)
                                    .reportadaPor(usuario)
                                    .prioridad(PrioridadIncidencia.MEDIA)
                                    .build());

                    match.setTitulo(cat);
                    match.setDescripcion(desc);
                    if ("Resuelta".equalsIgnoreCase(estadoStr)) {
                        match.setEstado(EstadoIncidencia.RESUELTA);
                        match.setSolucionAplicada(solucion != null ? solucion : "Resuelto");
                        if (match.getFechaSolucion() == null) match.setFechaSolucion(LocalDateTime.now());
                    } else {
                        match.setEstado(EstadoIncidencia.REPORTADA);
                    }
                    incidenciaRepository.save(match);
                }
            }

            // 7. Sincronizar Usuarios (`usuarios`)
            List<Map<String, Object>> users = (List<Map<String, Object>>) state.get("users");
            if (users != null) {
                for (Map<String, Object> u : users) {
                    String username = String.valueOf(u.getOrDefault("usuario", "")).trim();
                    if (username.isEmpty()) continue;
                    String nombre = String.valueOf(u.getOrDefault("nombre", username));
                    String rolStr = String.valueOf(u.getOrDefault("rol", "Recepcionista"));
                    String passRaw = String.valueOf(u.getOrDefault("passwordHash", "Hotel123"));
                    boolean activo = !"Inactivo".equalsIgnoreCase(String.valueOf(u.getOrDefault("estado", "Activo")));

                    RolNombre rn = mapRolFromFrontend(rolStr);
                    Rol rolEntity = rolRepository.findByNombre(rn).orElse(null);

                    Usuario usr = usuarioRepository.findByUsername(username).orElseGet(() -> Usuario.builder()
                            .username(username)
                            .email(username + "@hotelaventura.pe")
                            .password(passwordEncoder.encode(passRaw))
                            .build());
                    usr.setNombreCompleto(nombre);
                    usr.setActivo(activo);
                    if (rolEntity != null) {
                        usr.setRoles(new HashSet<>(Collections.singletonList(rolEntity)));
                    }
                    usuarioRepository.save(usr);
                }
            }
        }

        return ResponseEntity.ok("{\"ok\":true,\"version\":" + System.currentTimeMillis() + "}");
    }

    @DeleteMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<String> limpiarDatosOperativos() {
        incidenciaRepository.deleteAll();
        reservaRepository.deleteAll();
        huespedRepository.deleteAll();
        sistemaColeccionRepository.deleteAll();
        return ResponseEntity.ok("{\"ok\":true}");
    }

    private String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Boolean || obj instanceof Number) return String.valueOf(obj);
        if (obj instanceof String s) {
            StringBuilder sb = new StringBuilder("\"");
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                switch (ch) {
                    case '"' -> sb.append("\\\"");
                    case '\\' -> sb.append("\\\\");
                    case '\b' -> sb.append("\\b");
                    case '\f' -> sb.append("\\f");
                    case '\n' -> sb.append("\\n");
                    case '\r' -> sb.append("\\r");
                    case '\t' -> sb.append("\\t");
                    default -> {
                        if (ch < ' ') {
                            sb.append(String.format("\\u%04x", (int) ch));
                        } else {
                            sb.append(ch);
                        }
                    }
                }
            }
            sb.append("\"");
            return sb.toString();
        }
        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(String.valueOf(entry.getKey()))).append(":").append(toJson(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Iterable<?> iter) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : iter) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(item));
            }
            sb.append("]");
            return sb.toString();
        }
        return toJson(String.valueOf(obj));
    }

    private String mapDefaultPasswordForUser(String username) {
        return switch (username.toLowerCase()) {
            case "admin" -> "Admin123";
            case "recepcion1" -> "Recep123";
            case "limpieza1" -> "Limp1234";
            case "cajero1" -> "Caja1234";
            case "erik1" -> "Erik1234";
            default -> "Hotel123";
        };
    }

    private String mapRolToFrontend(Set<Rol> roles) {
        if (roles == null || roles.isEmpty()) return "Recepcionista";
        for (Rol r : roles) {
            if (r.getNombre() == RolNombre.ADMIN) return "Administrador";
            if (r.getNombre() == RolNombre.HOUSEKEEPING || r.getNombre() == RolNombre.MANTENIMIENTO) {
                return "Personal de Limpieza";
            }
        }
        return "Recepcionista";
    }

    private RolNombre mapRolFromFrontend(String rolStr) {
        if ("Administrador".equalsIgnoreCase(rolStr)) return RolNombre.ADMIN;
        if ("Personal de Limpieza".equalsIgnoreCase(rolStr)) return RolNombre.HOUSEKEEPING;
        return RolNombre.RECEPCIONISTA;
    }

    private String mapEstadoHabitacionToFrontend(Habitacion h) {
        if (h.getObservaciones() != null && List.of("Disponible", "Reservada", "Ocupada", "En Limpieza", "Mantenimiento").contains(h.getObservaciones())) {
            return h.getObservaciones();
        }
        if (h.getEstadoOperativo() == EstadoOperativo.OCUPADA) return "Ocupada";
        if (h.getEstadoOperativo() == EstadoOperativo.FUERA_DE_SERVICIO || h.getEstadoOperativo() == EstadoOperativo.BLOQUEADA) return "Mantenimiento";
        if (h.getEstadoLimpieza() == EstadoLimpieza.SUCIA) return "En Limpieza";
        return "Disponible";
    }

    private String mapTipoDocToFrontend(TipoDocumento td) {
        if (td == null) return "DNI";
        return switch (td) {
            case RUC -> "RUC";
            case PASAPORTE -> "Pasaporte";
            case CARNET_EXTRANJERIA -> "Carné de Extranjería";
            default -> "DNI";
        };
    }

    private TipoDocumento mapTipoDocFromFrontend(String s) {
        if ("RUC".equalsIgnoreCase(s)) return TipoDocumento.RUC;
        if ("Pasaporte".equalsIgnoreCase(s)) return TipoDocumento.PASAPORTE;
        if (s != null && s.toLowerCase().contains("extranjer")) return TipoDocumento.CARNET_EXTRANJERIA;
        return TipoDocumento.DNI;
    }

    private String mapEstadoReservaToFrontend(EstadoReserva er) {
        if (er == null) return "Confirmada";
        return switch (er) {
            case EN_CASA -> "En Curso";
            case FINALIZADA -> "Finalizada";
            case CANCELADA -> "Cancelada";
            case NO_SHOW -> "No Presentada";
            case PENDIENTE -> "Pendiente";
            default -> "Confirmada";
        };
    }

    private EstadoReserva mapEstadoReservaFromFrontend(String s) {
        if ("En Curso".equalsIgnoreCase(s)) return EstadoReserva.EN_CASA;
        if ("Finalizada".equalsIgnoreCase(s)) return EstadoReserva.FINALIZADA;
        if ("Cancelada".equalsIgnoreCase(s)) return EstadoReserva.CANCELADA;
        if ("No Presentada".equalsIgnoreCase(s)) return EstadoReserva.NO_SHOW;
        if ("Pendiente".equalsIgnoreCase(s)) return EstadoReserva.PENDIENTE;
        return EstadoReserva.CONFIRMADA;
    }

    private String mapCategoriaProductoToFrontend(CategoriaProducto cp) {
        if (cp == null) return "Minibar";
        return switch (cp) {
            case LAVANDERIA -> "Lavandería";
            case RESTAURANTE -> "Restaurante";
            case SERVICIOS -> "Spa";
            case BEBIDAS -> "Bebidas";
            case SNACKS -> "Snacks";
            default -> "Minibar";
        };
    }

    private CategoriaProducto mapCategoriaProductoFromFrontend(String s) {
        if (s == null) return CategoriaProducto.MINIBAR;
        String lower = s.toLowerCase();
        if (lower.contains("lavander")) return CategoriaProducto.LAVANDERIA;
        if (lower.contains("restauran")) return CategoriaProducto.RESTAURANTE;
        if (lower.contains("spa") || lower.contains("servici")) return CategoriaProducto.SERVICIOS;
        if (lower.contains("bebida")) return CategoriaProducto.BEBIDAS;
        if (lower.contains("snack")) return CategoriaProducto.SNACKS;
        return CategoriaProducto.MINIBAR;
    }

    private int toInt(Object val, int def) {
        if (val == null) return def;
        if (val instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (Exception e) {
            return def;
        }
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(String.valueOf(val));
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object val, String def) {
        if (val == null) return new BigDecimal(def);
        try {
            return new BigDecimal(String.valueOf(val));
        } catch (Exception e) {
            return new BigDecimal(def);
        }
    }

    private LocalDate parseLocalDate(Object val, LocalDate def) {
        if (val == null) return def;
        try {
            return LocalDate.parse(String.valueOf(val).substring(0, 10));
        } catch (Exception e) {
            return def;
        }
    }
}
