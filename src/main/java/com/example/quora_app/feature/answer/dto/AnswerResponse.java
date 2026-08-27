package com.example.quora_app.feature.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private UUID id;
    private String content;

    private UUID userId;
    private String username;

    private UUID questionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
