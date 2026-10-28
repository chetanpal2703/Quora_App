package com.example.quora_app.feature.auth.service;

import com.example.quora_app.feature.auth.dto.LoginRequest;
import com.example.quora_app.feature.auth.dto.LoginResponse;
import com.example.quora_app.feature.auth.dto.LoginResult;
import com.example.quora_app.feature.auth.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;

public interface AuthService {
    LoginResult login(LoginRequest request);

    LoginResult refresh(String refreshToken);

    void logout(String refreshToken);

    UserResponse register(UserRegistrationRequest request);
}
