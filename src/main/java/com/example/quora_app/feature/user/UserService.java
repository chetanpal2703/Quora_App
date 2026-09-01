package com.example.quora_app.feature.user;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.core.common.dto.PageResponse;
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

    UserResponse getUserById(UUID id);

    List<UserResponse> getAllUsersWithoutParams();

    UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest userUpdateRequest);

    void deleteUser(@PathVariable UUID id);

    PageResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir, String search);

}
