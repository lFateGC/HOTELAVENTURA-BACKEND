package com.hotelaventura.service.impl;

import com.hotelaventura.exception.BadRequestException;
import com.hotelaventura.exception.ResourceNotFoundException;
import com.hotelaventura.dto.HuespedDTO;
import com.hotelaventura.dto.RegistroHuespedDTO;
import com.hotelaventura.entity.Huesped;
import com.hotelaventura.entity.MotivoViaje;
import com.hotelaventura.entity.RegistroHuesped;
import com.hotelaventura.entity.TipoDocumento;
import com.hotelaventura.repository.HuespedRepository;
import com.hotelaventura.repository.RegistroHuespedRepository;
import com.hotelaventura.service.HuespedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;
    private final RegistroHuespedRepository registroHuespedRepository;

    @Override
    @Transactional
    public HuespedDTO registrar(HuespedDTO dto) {
        // Validamos por documento para no duplicar fichas de huespedes
        if (huespedRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new BadRequestException("Ya existe un huesped con el documento: " + dto.getNumeroDocumento());
        }

        TipoDocumento tipoDoc;
        try {
            tipoDoc = TipoDocumento.valueOf(dto.getTipoDocumento().toUpperCase());
        } catch (Exception e) {
            tipoDoc = TipoDocumento.DNI; // por defecto DNI
        }

        // Ficha oficial de registro tipo MINCETUR

        Huesped huesped = Huesped.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .tipoDocumento(tipoDoc)
                .numeroDocumento(dto.getNumeroDocumento())
                .fechaNacimiento(dto.getFechaNacimiento())
                .sexo(dto.getSexo())
                .nacionalidad(dto.getNacionalidad() != null ? dto.getNacionalidad() : "Peruana")
                .residencia(dto.getResidencia())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .preferencias(dto.getPreferencias())
                .build();

        return convertirADTO(huespedRepository.save(huesped));
    }

    @Override
    @Transactional
    public HuespedDTO actualizar(Long id, HuespedDTO dto) {
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Huesped no encontrado con ID: " + id));

        if (!huesped.getNumeroDocumento().equalsIgnoreCase(dto.getNumeroDocumento())
                && huespedRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new BadRequestException("El documento '" + dto.getNumeroDocumento() + "' ya pertenece a otro huesped");
        }

        huesped.setNombres(dto.getNombres());
        huesped.setApellidos(dto.getApellidos());
        huesped.setResidencia(dto.getResidencia());
        huesped.setTelefono(dto.getTelefono());
        huesped.setEmail(dto.getEmail());
        huesped.setPreferencias(dto.getPreferencias());

        return convertirADTO(huespedRepository.save(huesped));
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedDTO obtenerPorId(Long id) {
        return huespedRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Huesped no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public HuespedDTO obtenerPorDocumento(String numeroDocumento) {
        return huespedRepository.findByNumeroDocumento(numeroDocumento)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Huesped no encontrado con documento: " + numeroDocumento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HuespedDTO> listarTodos() {
        return huespedRepository.findAllByOrderByApellidosAsc().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HuespedDTO> buscarPorNombre(String termino) {
        return huespedRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(termino, termino).stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional
    public RegistroHuespedDTO registrarEnEstancia(RegistroHuespedDTO dto) {
        Huesped huesped = huespedRepository.findById(dto.getHuespedId())
                .orElseThrow(() -> new ResourceNotFoundException("Huesped no encontrado"));

        MotivoViaje motivo;
        try {
            motivo = MotivoViaje.valueOf(dto.getMotivoViaje().toUpperCase());
        } catch (Exception e) {
            motivo = MotivoViaje.TURISMO;
        }

        RegistroHuesped registro = RegistroHuesped.builder()
                .estanciaId(dto.getEstanciaId())
                .huesped(huesped)
                .motivoViaje(motivo)
                .esTitular(dto.getEsTitular() != null ? dto.getEsTitular() : true)
                .fechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDateTime.now())
                .observaciones(dto.getObservaciones())
                .build();

        RegistroHuesped guardado = registroHuespedRepository.save(registro);

        return RegistroHuespedDTO.builder()
                .id(guardado.getId())
                .estanciaId(guardado.getEstanciaId())
                .huespedId(huesped.getId())
                .huespedNombreCompleto(huesped.getNombres() + " " + huesped.getApellidos())
                .huespedDocumento(huesped.getNumeroDocumento())
                .motivoViaje(guardado.getMotivoViaje().name())
                .esTitular(guardado.getEsTitular())
                .fechaIngreso(guardado.getFechaIngreso())
                .observaciones(guardado.getObservaciones())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroHuespedDTO> listarPorEstancia(Long estanciaId) {
        return registroHuespedRepository.findByEstanciaId(estanciaId).stream()
                .map(r -> RegistroHuespedDTO.builder()
                        .id(r.getId())
                        .estanciaId(r.getEstanciaId())
                        .huespedId(r.getHuesped().getId())
                        .huespedNombreCompleto(r.getHuesped().getNombres() + " " + r.getHuesped().getApellidos())
                        .huespedDocumento(r.getHuesped().getNumeroDocumento())
                        .motivoViaje(r.getMotivoViaje().name())
                        .esTitular(r.getEsTitular())
                        .fechaIngreso(r.getFechaIngreso())
                        .fechaSalida(r.getFechaSalida())
                        .observaciones(r.getObservaciones())
                        .build())
                .toList();
    }

    private HuespedDTO convertirADTO(Huesped h) {
        return HuespedDTO.builder()
                .id(h.getId())
                .nombres(h.getNombres())
                .apellidos(h.getApellidos())
                .tipoDocumento(h.getTipoDocumento().name())
                .numeroDocumento(h.getNumeroDocumento())
                .fechaNacimiento(h.getFechaNacimiento())
                .sexo(h.getSexo())
                .nacionalidad(h.getNacionalidad())
                .residencia(h.getResidencia())
                .telefono(h.getTelefono())
                .email(h.getEmail())
                .activo(h.getActivo())
                .fechaRegistro(h.getFechaRegistro())
                .preferencias(h.getPreferencias())
                .build();
    }
}
