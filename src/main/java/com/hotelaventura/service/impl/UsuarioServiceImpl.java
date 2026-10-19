package com.hotelaventura.service.impl;

import com.hotelaventura.dto.RegistroUsuarioRequest;
import com.hotelaventura.dto.UsuarioDTO;
import com.hotelaventura.entity.Rol;
import com.hotelaventura.entity.RolNombre;
import com.hotelaventura.entity.Usuario;
import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.repository.RolRepository;
import com.hotelaventura.repository.UsuarioRepository;
import com.hotelaventura.service.AuditoriaService;
import com.hotelaventura.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder,
                              AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Override
    @Transactional
    public UsuarioDTO registrar(RegistroUsuarioRequest request, String ip) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("El nombre de usuario '" + request.getUsername() + "' ya esta en uso");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El correo '" + request.getEmail() + "' ya esta registrado");
        }

        Set<Rol> roles = new HashSet<>();
        for (String rolStr : request.getRoles()) {
            try {
                RolNombre rolNombre = RolNombre.valueOf(rolStr.toUpperCase());
                Rol rol = rolRepository.findByNombre(rolNombre)
                        .orElseGet(() -> rolRepository.save(Rol.builder()
                                .nombre(rolNombre)
                                .descripcion("Rol de " + rolNombre.name())
                                .build()));
                roles.add(rol);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Rol no valido: " + rolStr);
            }
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .roles(roles)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(
                "SISTEMA",
                "CREAR_USUARIO",
                "Usuario",
                guardado.getId(),
                "Usuario creado: " + guardado.getUsername() + " con roles: " + request.getRoles(),
                ip
        );

        return convertirADTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return convertirADTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO cambiarEstado(Long id, boolean activo, String usuarioModificador, String ip) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        usuario.setActivo(activo);
        Usuario actualizado = usuarioRepository.save(usuario);

        auditoriaService.registrarAccion(
                usuarioModificador,
                activo ? "ACTIVAR_USUARIO" : "DESACTIVAR_USUARIO",
                "Usuario",
                actualizado.getId(),
                "Estado de usuario " + actualizado.getUsername() + " actualizado a: " + (activo ? "ACTIVO" : "INACTIVO"),
                ip
        );

        return convertirADTO(actualizado);
    }

    private UsuarioDTO convertirADTO(Usuario u) {
        return UsuarioDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .nombreCompleto(u.getNombreCompleto())
                .activo(u.getActivo())
                .fechaCreacion(u.getFechaCreacion())
                .ultimoAcceso(u.getUltimoAcceso())
                .roles(u.getRoles().stream()
                        .map(r -> r.getNombre().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}
