package com.example.quora_app.feature.auth.service;
import com.example.quora_app.feature.user.User;

public interface RefreshTokenService {
    IssuedRefreshToken createInitialToken(User user);

    RotatedRefreshToken rotate(String rawToken);

    void revokeFamily(String rawToken);

    record IssuedRefreshToken(String rawToken, long maxAgeSeconds) {
    }

    record RotatedRefreshToken(
            boolean valid,
            String rawToken,
            String username,
            long maxAgeSeconds
    ) {
    }
}
