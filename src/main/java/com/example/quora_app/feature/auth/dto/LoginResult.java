package com.example.quora_app.feature.auth.dto;

public record LoginResult(
        LoginResponse response,
        String accessToken,
        String refreshToken,
        long refreshTokenMaxAgeSeconds
) {}
