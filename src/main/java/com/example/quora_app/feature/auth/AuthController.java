package com.example.quora_app.feature.auth;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.auth.dto.LoginRequest;
import com.example.quora_app.feature.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);

        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(loginResponse)
                        .build();

        return ResponseEntity.ok(response);
    }
}
