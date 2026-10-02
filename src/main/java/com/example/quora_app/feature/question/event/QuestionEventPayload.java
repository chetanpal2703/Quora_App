package com.example.quora_app.feature.question.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEventPayload {
    private UUID id;

    private String title;

    private String content;

    private UUID userId;

    private String username;

    private Set<String> tags;

    private Long version;

    private Instant createdAt;

    private Instant updatedAt;
}
