package com.example.quora_app.feature.auth.service;

import com.example.quora_app.core.exception.InvalidRefreshTokenException;
import com.example.quora_app.core.exception.ResourceAlreadyExistsException;
import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.core.security.CustomUserDetails;
import com.example.quora_app.core.security.CustomUserDetailsService;
import com.example.quora_app.core.security.JwtService;
import com.example.quora_app.feature.auth.dto.LoginRequest;
import com.example.quora_app.feature.auth.dto.LoginResponse;
import com.example.quora_app.feature.auth.dto.LoginResult;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.user.UserRepository;
import com.example.quora_app.feature.auth.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import com.example.quora_app.feature.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

    @Value("${app.auth.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public LoginResult login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new  ResourceNotFoundException("Authenticated user was not found"));

        String accessToken = jwtService.generateToken(userDetails);

        RefreshTokenService.IssuedRefreshToken refreshToken = refreshTokenService.createInitialToken(user);

        return new LoginResult(
                LoginResponse.builder().expiresInSeconds(jwtService.getExpiration() / 1000).build(),
                accessToken,
                refreshToken.rawToken(),
                refreshToken.maxAgeSeconds()
        );
    }

    /*
     * Intentionally not @Transactional.
     *
     * RefreshTokenService.rotate() must finish its transaction before
     * an invalid result becomes an exception here.
     */
    @Override
    public LoginResult refresh(String rawRefreshToken) {

        RefreshTokenService.RotatedRefreshToken rotated = refreshTokenService.rotate(rawRefreshToken);

        if (!rotated.valid()) {
            throw new InvalidRefreshTokenException();
        }

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(rotated.username());

        String newAccessToken = jwtService.generateToken(userDetails);

        return new LoginResult(
                LoginResponse.builder()
                        .expiresInSeconds(jwtService.getExpiration() / 1000)
                        .build(),
                newAccessToken,
                rotated.rawToken(),
                rotated.maxAgeSeconds()
        );
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenService.revokeFamily(rawRefreshToken);
    }

    @Override
    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
