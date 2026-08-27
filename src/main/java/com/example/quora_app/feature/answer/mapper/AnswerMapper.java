package com.example.quora_app.feature.answer.mapper;

import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.answer.dto.AnswerResponse;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {
    public AnswerResponse toResponse(Answer answer) {

        return AnswerResponse.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .userId(answer.getUser().getId())
                .username(answer.getUser().getUsername())
                .questionId(answer.getQuestion().getId())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}
