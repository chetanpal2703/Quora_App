package com.example.quora_app.feature.user;

import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponse register(UserRegistrationRequest request) {
        if(userRepository.existsByUsername(request.getName())){
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
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
}
