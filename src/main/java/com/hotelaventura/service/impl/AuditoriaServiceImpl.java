package com.hotelaventura.service.impl;

import com.hotelaventura.dto.AuditoriaDTO;
import com.hotelaventura.entity.Auditoria;
import com.hotelaventura.repository.AuditoriaRepository;
import com.hotelaventura.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    @Transactional
    public void registrarAccion(String usuario, String accion, String entidad, Long entidadId, String detalle, String ip) {
        Auditoria audit = Auditoria.builder()
                .usuario(usuario != null ? usuario : "SYSTEM")
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .detalle(detalle)
                .ip(ip)
                .fechaHora(LocalDateTime.now())
                .build();
        auditoriaRepository.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarTodas() {
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
    public List<AuditoriaDTO> listarPorAccion(String accion) {
        return auditoriaRepository.findByAccionOrderByFechaHoraDesc(accion).stream()
                .map(this::convertirADTO)
                .toList();
    }

    private AuditoriaDTO convertirADTO(Auditoria a) {
        return AuditoriaDTO.builder()
                .id(a.getId())
                .usuario(a.getUsuario())
                .accion(a.getAccion())
                .entidad(a.getEntidad())
                .entidadId(a.getEntidadId())
                .detalle(a.getDetalle())
                .ip(a.getIp())
                .fechaHora(a.getFechaHora())
                .build();
    }
}
