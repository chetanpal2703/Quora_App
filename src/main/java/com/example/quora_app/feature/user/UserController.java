package com.example.quora_app.feature.user;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import com.example.quora_app.feature.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserResponse userResponse = userService.register(userRegistrationRequest);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUser() {
        List<UserResponse> userResponses=userService.getAllUsersWithoutParams();
        ApiResponse<List<UserResponse>> response= ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("User fetched successfully")
                .data(userResponses)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse userResponse=userService.getUserById(id);
        ApiResponse<UserResponse> response=ApiResponse.<UserResponse>builder()
                .success(true)
                .message("user found Successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>>  updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser=userService.updateUser(id,userUpdateRequest);
        ApiResponse<UserResponse> response=ApiResponse.<UserResponse>builder()
                .success(true)
                .message("user updated Successfully")
                .data(updatedUser)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search
    ) {
        PageResponse<UserResponse> users = userService.getAllUsers(page, size, sortBy, sortDir, search);

        ApiResponse<PageResponse<UserResponse>> response = ApiResponse.<PageResponse<UserResponse>>builder()
                        .success(true)
                        .message("Users fetched successfully")
                        .data(users)
                        .build();
        return ResponseEntity.ok(response);
    }
}
