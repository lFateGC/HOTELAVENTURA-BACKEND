package com.hotelaventura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private Set<String> roles;
}
