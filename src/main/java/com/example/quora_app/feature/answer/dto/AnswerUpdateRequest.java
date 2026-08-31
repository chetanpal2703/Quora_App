package com.example.quora_app.feature.answer.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerUpdateRequest {
    @Size(min = 10, message = "Answer must be at least 10 characters")
    private String content;
}
