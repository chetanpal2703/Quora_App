package com.example.quora_app.feature.user;

import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;

public interface UserService {
    UserResponse register(UserRegistrationRequest request);
}
