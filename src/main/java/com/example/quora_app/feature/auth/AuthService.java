package com.example.quora_app.feature.auth;

import com.example.quora_app.feature.auth.dto.LoginRequest;
import com.example.quora_app.feature.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
