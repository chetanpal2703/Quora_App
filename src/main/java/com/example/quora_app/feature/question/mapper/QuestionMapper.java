package com.example.quora_app.feature.question.mapper;

import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import com.example.quora_app.feature.tag.Tag;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {
    public QuestionResponse toResponse(Question question) {

        Set<String> tags = question.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUser().getId())
                .username(question.getUser().getUsername())
                .tags(tags)
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
