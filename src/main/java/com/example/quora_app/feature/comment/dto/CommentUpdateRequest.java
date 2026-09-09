package com.example.quora_app.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {

    @NotBlank(message = "Content is required and cannot be empty")
    @Size(min = 2, max = 2000, message = "Comment must be between 2 and 2000 characters")
    private String content;

}
