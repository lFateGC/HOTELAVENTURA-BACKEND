package com.hotelaventura.service;

import com.hotelaventura.dto.RegistroUsuarioRequest;
import com.hotelaventura.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioService {
    UsuarioDTO registrar(RegistroUsuarioRequest request, String ip);
    List<UsuarioDTO> listarTodos();
    UsuarioDTO obtenerPorId(Long id);
    UsuarioDTO cambiarEstado(Long id, boolean activo, String usuarioModificador, String ip);
}
