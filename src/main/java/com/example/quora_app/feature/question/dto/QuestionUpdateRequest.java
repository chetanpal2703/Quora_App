package com.example.quora_app.feature.question.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionUpdateRequest {
    @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
    private String title;

    @Size(min = 10, message = "Content must be at least 10 characters")
    private String content;
}
