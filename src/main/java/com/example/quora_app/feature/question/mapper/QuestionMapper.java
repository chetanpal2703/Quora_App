package com.example.quora_app.feature.question.mapper;

import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {
    public QuestionResponse toResponse(Question question) {

        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUser().getId())
                .username(question.getUser().getUsername())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
