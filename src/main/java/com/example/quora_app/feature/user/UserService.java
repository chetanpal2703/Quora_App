package com.example.quora_app.feature.user;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import com.example.quora_app.feature.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse register(UserRegistrationRequest request);

    UserResponse getUserById(UUID id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest userUpdateRequest);

    UserResponse deleteUser(@PathVariable UUID id);

}
