package com.example.quora_app.feature.question.mapper;

import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.event.QuestionEventPayload;
import com.example.quora_app.feature.tag.Tag;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Component
public class QuestionEventPayloadMapper {
    private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");

    public QuestionEventPayload toPayload(Question question) {
        return QuestionEventPayload.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUser().getId())
                .username(question.getUser().getUsername())
                .tags(
                        question.getTags()
                                .stream()
                                .map(Tag::getName)
                                .collect(Collectors.toSet())
                )
                .version(question.getVersion())
                .createdAt(toInstant(question.getCreatedAt()))
                .updatedAt(toInstant(question.getUpdatedAt()))
                .build();
    }

    private Instant toInstant(java.time.LocalDateTime dateTime) {

        if (dateTime == null) {
            return null;
        }

        return dateTime
                .atZone(INDIA_ZONE)
                .toInstant();
    }
}
