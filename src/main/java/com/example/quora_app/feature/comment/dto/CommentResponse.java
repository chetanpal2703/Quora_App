package com.example.quora_app.feature.comment.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private UUID id;
    private String content;

    private UUID userId;
    private String username;

    private UUID questionId;
    private UUID answerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
