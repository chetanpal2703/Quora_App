package com.example.quora_app.feature.auth;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.auth.dto.LoginRequest;
import com.example.quora_app.feature.auth.dto.LoginResponse;

import com.example.quora_app.feature.auth.dto.LoginResult;
import com.example.quora_app.feature.auth.dto.UserRegistrationRequest;
import com.example.quora_app.feature.auth.service.AuthService;
import com.example.quora_app.feature.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Value("${app.auth.cookie.secure:false}")
    private boolean secureCookie;

    @Value("${app.auth.cookie.same-site:Lax}")
    private String sameSite;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResult result = authService.login(request);
        return tokenResponse(result, "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        LoginResult result = authService.refresh(refreshToken);
        return tokenResponse(result, "Token refreshed successfully");
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        ResponseCookie clearAccessCookie = ResponseCookie
                .from("access_token", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite(sameSite)
                .path("/api")
                .maxAge(Duration.ZERO)
                .build();

        ResponseCookie clearRefreshCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite(sameSite)
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString())
                .build();
    }

    private ResponseEntity<ApiResponse<LoginResponse>> tokenResponse(LoginResult result, String message) {
        ResponseCookie accessCookie = ResponseCookie
                .from("access_token", result.accessToken())
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite(sameSite)
                .path("/api")
                .maxAge(Duration.ofSeconds(result.response().getExpiresInSeconds()))
                .build();

        ResponseCookie refreshCookie = ResponseCookie
                .from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite(sameSite)
                .path("/api/v1/auth")
                .maxAge(Duration.ofSeconds(result.refreshTokenMaxAgeSeconds()))
                .build();

        ApiResponse<LoginResponse> body = ApiResponse
                .<LoginResponse>builder()
                .success(true)
                .message(message)
                .data(result.response())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(body);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserResponse userResponse = authService.register(userRegistrationRequest);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
