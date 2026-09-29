package com.example.quora_app.feature.question.event;

import java.util.UUID;

public record QuestionCreatedEvent(UUID questionId) {
}
