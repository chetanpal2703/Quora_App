package com.example.quora_app.feature.user;

import com.example.quora_app.core.exception.GlobalExceptionHandler;
import com.example.quora_app.core.exception.ResourceAlreadyExistsException;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        // Arrange
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .name("Chetan Pal")
                .username("testuser")
                .email("test@example.com")
                .build();

        when(userService.register(any(UserRegistrationRequest.class)))
                .thenReturn(response);

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("Chetan Pal")
                .username("testuser")
                .email("test@example.com")
                .password("securePassword123!")
                .build();

        // Act + Assert
        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"))
                .andExpect(jsonPath("$.data.name")
                        .value("Chetan Pal"))
                .andExpect(jsonPath("$.data.username")
                        .value("testuser"))
                .andExpect(jsonPath("$.data.email")
                        .value("test@example.com"));

        // Verify controller called the service
        verify(userService).register(any(UserRegistrationRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        // Arrange
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("")
                .username("")
                .email("invalid-email")
                .password("123")
                .build();

        // Act + Assert
        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.username").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.password").exists());

        // Validation should stop execution before service
        verify(userService, never())
                .register(any(UserRegistrationRequest.class));
    }

    @Test
    void shouldReturnConflictWhenUsernameAlreadyExists() throws Exception {

        // Arrange
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("Chetan Pal")
                .username("testuser")
                .email("test@example.com")
                .password("securePassword123!")
                .build();

        when(userService.register(any(UserRegistrationRequest.class)))
                .thenThrow(
                        new ResourceAlreadyExistsException(
                                "Username already exists"
                        )
                );

        // Act + Assert
        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Username already exists"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

        // Arrange
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("Chetan Pal")
                .username("testuser")
                .email("test@example.com")
                .password("securePassword123!")
                .build();

        when(userService.register(any(UserRegistrationRequest.class)))
                .thenThrow(
                        new ResourceAlreadyExistsException(
                                "Email already exists"
                        )
                );

        // Act + Assert
        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Email already exists"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}