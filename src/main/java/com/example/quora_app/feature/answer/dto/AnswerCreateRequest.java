package com.example.quora_app.feature.answer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCreateRequest {
    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Answer must be at least 10 characters")
    private String content;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Question ID is required")
    private UUID questionId;
}
