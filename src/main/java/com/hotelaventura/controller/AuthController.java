package com.hotelaventura.controller;

import com.hotelaventura.dto.AuthResponse;
import com.hotelaventura.dto.LoginRequest;
import com.hotelaventura.dto.RegistroUsuarioRequest;
import com.hotelaventura.dto.UsuarioDTO;
import com.hotelaventura.service.AuthService;
import com.hotelaventura.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ip));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroUsuarioRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return new ResponseEntity<>(usuarioService.registrar(request, ip), HttpStatus.CREATED);
    }
}
