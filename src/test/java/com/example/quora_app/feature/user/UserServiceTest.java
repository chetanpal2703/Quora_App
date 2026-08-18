package com.example.quora_app.feature.user;

import com.example.quora_app.core.exception.ResourceAlreadyExistsException;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
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

        // Arrange
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

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

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

        // Verify password was encoded
        verify(passwordEncoder).encode(validRequest.getPassword());

        // Capture the actual User passed to save()
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        // Verify correct data was mapped to entity
        assertEquals(validRequest.getName(), userToSave.getName());
        assertEquals(validRequest.getUsername(), userToSave.getUsername());
        assertEquals(validRequest.getEmail(), userToSave.getEmail());

        // Important: raw password must NOT be stored
        assertEquals("hashed_password", userToSave.getPassword());
        assertNotEquals(validRequest.getPassword(), userToSave.getPassword());

        // Make sure save happened only once
        verify(userRepository, times(1)).save(any(User.class));
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

        // Since username already exists, these should never happen
        verify(userRepository, never())
                .existsByEmail(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
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

        // Password should not be encoded
        verify(passwordEncoder, never())
                .encode(anyString());

        // User should not be saved
        verify(userRepository, never())
                .save(any(User.class));
    }
}