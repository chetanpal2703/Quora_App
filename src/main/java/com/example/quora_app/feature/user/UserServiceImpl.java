package com.example.quora_app.feature.user;

import com.example.quora_app.core.exception.ResourceAlreadyExistsException;
import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import com.example.quora_app.feature.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user=User.builder().name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .build();

        User savedUser = userRepository.save(user);
        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .build();
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("user not found"));
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user->UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username((user.getUsername()))
                        .email(user.getEmail())
                        .build()
                ).toList();
    }

    @Override
    public UserResponse updateUser(UUID id, UserUpdateRequest userUpdateRequest) {
        User user =userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("user not found"));
        if(userUpdateRequest.getUsername()!=null){
            user.setUsername(userUpdateRequest.getUsername());
        }
        if(userUpdateRequest.getEmail()!=null){
            user.setEmail(userUpdateRequest.getEmail());
        }
        if(userUpdateRequest.getName()!=null){
            user.setName(userUpdateRequest.getName());
        }
        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .build();
    }

    @Override
    public UserResponse deleteUser(UUID id) {
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("user not found"));
        userRepository.delete(user);
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
