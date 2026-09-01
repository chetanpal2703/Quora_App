package com.example.quora_app.feature.auth;

import com.example.quora_app.feature.auth.dto.LoginRequest;
import com.example.quora_app.feature.auth.dto.LoginResponse;
import com.example.quora_app.feature.auth.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UserResponse register(UserRegistrationRequest request);
}
