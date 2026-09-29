package com.example.quora_app.feature.question.search.mapper;

import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.search.QuestionSearchDocument;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class QuestionSearchDocumentMapper {
    public QuestionSearchDocument toDocument(Question question) {

        return QuestionSearchDocument.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUser().getId())
                .username(question.getUser().getUsername())
                .tags(
                        question.getTags()
                                .stream()
                                .map(tag -> tag.getName())
                                .collect(java.util.stream.Collectors.toSet())
                )
                // ADD THE TIMEZONE CONVERSION HERE:
                .createdAt(question.getCreatedAt()
                        .atZone(ZoneId.of("Asia/Kolkata"))
                        .toInstant())
                .build();
    }
}
