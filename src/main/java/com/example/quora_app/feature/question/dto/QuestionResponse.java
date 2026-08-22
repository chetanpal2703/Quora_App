package com.example.quora_app.feature.question.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private UUID id;
    private String title;
    private String content;

    private UUID userId;
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}