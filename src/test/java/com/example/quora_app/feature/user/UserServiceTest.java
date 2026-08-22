package com.example.quora_app.feature.user;

import com.example.quora_app.core.exception.ResourceAlreadyExistsException;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import com.example.quora_app.feature.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = UserRegistrationRequest.builder()
                .name("Chetan Pal")
                .username("testuser")
                .email("test@example.com")
                .password("securePassword123!")
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // 1. Arrange - Setup Repository and Encoder
        when(userRepository.existsByUsername(validRequest.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(validRequest.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(validRequest.getPassword()))
                .thenReturn("hashed_password");

        User savedUser = User.builder()
                .name(validRequest.getName())
                .username(validRequest.getUsername())
                .email(validRequest.getEmail())
                .password("hashed_password")
                .build();
        savedUser.setId(UUID.randomUUID());

        when(userRepository.save(any())).thenReturn(savedUser);

        // 2. Arrange - Setup the expected UserResponse
        UserResponse expectedResponse = UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();

        // 3. Arrange - Mock the mapper behavior
        when(userMapper.toResponse(any()))
                .thenReturn(expectedResponse);

        // Act
        UserResponse response = userService.register(validRequest);

        // Assert - response
        assertNotNull(response);
        assertEquals(validRequest.getName(), response.getName());
        assertEquals(validRequest.getUsername(), response.getUsername());
        assertEquals(validRequest.getEmail(), response.getEmail());
        assertEquals(savedUser.getId(), response.getId());

        // Verify repository checks
        verify(userRepository).existsByUsername(validRequest.getUsername());
        verify(userRepository).existsByEmail(validRequest.getEmail());
        verify(passwordEncoder).encode(validRequest.getPassword());
        verify(userMapper).toResponse(any());

        // Capture the actual User passed to save()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User userToSave = userCaptor.getValue();

        // Verify correct data was mapped to entity
        assertEquals(validRequest.getName(), userToSave.getName());
        assertEquals(validRequest.getUsername(), userToSave.getUsername());
        assertEquals(validRequest.getEmail(), userToSave.getEmail());
        assertEquals("hashed_password", userToSave.getPassword());
        assertNotEquals(validRequest.getPassword(), userToSave.getPassword());

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(validRequest.getUsername()))
                .thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception =
                assertThrows(
                        ResourceAlreadyExistsException.class,
                        () -> userService.register(validRequest)
                );

        assertTrue(exception.getMessage().contains("Username"));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(validRequest.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(validRequest.getEmail()))
                .thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception =
                assertThrows(
                        ResourceAlreadyExistsException.class,
                        () -> userService.register(validRequest)
                );

        assertTrue(exception.getMessage().contains("Email"));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toResponse(any());
    }
}