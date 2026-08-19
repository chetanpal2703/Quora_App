package com.example.quora_app.feature.user;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import com.example.quora_app.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUserSuccessfully() {

        UserRegistrationRequest request =
                UserRegistrationRequest.builder()
                        .name("Chetan Pal")
                        .username("integrationuser")
                        .email("integration@example.com")
                        .password("Password123!")
                        .build();

        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        ResponseEntity<ApiResponse<UserResponse>> response =
                client.post()
                        .uri("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .toEntity(
                                new ParameterizedTypeReference<ApiResponse<UserResponse>>() {
                                }
                        );

        assertEquals(201, response.getStatusCode().value());

        ApiResponse<UserResponse> body = response.getBody();

        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(
                "User registered successfully",
                body.getMessage()
        );

        assertNotNull(body.getData());

        assertEquals(
                "integrationuser",
                body.getData().getUsername()
        );

        assertEquals(
                "integration@example.com",
                body.getData().getEmail()
        );

        // Verify the actual database state
        User savedUser = userRepository
                .findByUsername("integrationuser")
                .orElseThrow();

        assertEquals(
                "Chetan Pal",
                savedUser.getName()
        );

        assertEquals(
                "integration@example.com",
                savedUser.getEmail()
        );

        // Raw password must never be stored
        assertNotEquals(
                "Password123!",
                savedUser.getPassword()
        );

        // JPA auditing should populate these
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }
}