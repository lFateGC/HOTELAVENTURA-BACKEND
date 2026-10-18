package com.hotelaventura.service;

import com.hotelaventura.dto.AuthResponse;
import com.hotelaventura.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request, String ip);
}
