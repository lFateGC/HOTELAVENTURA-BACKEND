package com.hotelaventura.modulo_seguridad.service.impl;

import com.hotelaventura.modulo_seguridad.dto.AuditoriaDTO;
import com.hotelaventura.modulo_seguridad.entity.Auditoria;
import com.hotelaventura.modulo_seguridad.repository.AuditoriaRepository;
import com.hotelaventura.modulo_seguridad.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    @Override
    @Transactional
    public void registrarAccion(String usuario, String accion, String entidad, Long idEntidad, String detalle, String ip) {
        Auditoria auditoria = Auditoria.builder()
                .usuario(usuario != null ? usuario : "SISTEMA")
                .accion(accion)
                .entidad(entidad)
                .idEntidad(idEntidad)
                .detalle(detalle)
                .ip(ip)
                .fechaHora(LocalDateTime.now())
                .build();
        auditoriaRepository.save(auditoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarTodo() {
        return auditoriaRepository.findAllByOrderByFechaHoraDesc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarPorUsuario(String usuario) {
        return auditoriaRepository.findByUsuarioOrderByFechaHoraDesc(usuario).stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidadOrderByFechaHoraDesc(entidad).stream()
                .map(this::convertirADTO)
                .toList();
    }

    private AuditoriaDTO convertirADTO(Auditoria a) {
        return AuditoriaDTO.builder()
                .id(a.getId())
                .usuario(a.getUsuario())
                .accion(a.getAccion())
                .entidad(a.getEntidad())
                .idEntidad(a.getIdEntidad())
                .detalle(a.getDetalle())
                .ip(a.getIp())
                .fechaHora(a.getFechaHora())
                .build();
    }
}
