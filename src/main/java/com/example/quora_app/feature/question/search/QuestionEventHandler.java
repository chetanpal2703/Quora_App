package com.example.quora_app.feature.question.search;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.quora_app.feature.question.event.QuestionDeletedEventPayload;
import com.example.quora_app.feature.question.event.QuestionEventPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionEventHandler {
    private final QuestionSearchService questionSearchService;
    private final ObjectMapper objectMapper;

    /**
     * Handles a JSON event received from Kafka.
     * Exceptions intentionally propagate so Kafka's error handling
     * can retry events that fail to process.
     */
    public void handle(String message) throws Exception {
        JsonNode root = objectMapper.readTree(message);
        String eventType = root.path("eventType").asText();
        JsonNode data = root.path("data");

        if (eventType.isBlank() || data.isMissingNode() || data.isNull()) {
            throw new IllegalArgumentException(
                    "Invalid question event: eventType or data is missing"
            );
        }

        switch (eventType) {
            case "QUESTION_CREATED" -> {
                QuestionEventPayload question = objectMapper.treeToValue(data, QuestionEventPayload.class);
                indexQuestion(question);
            }

            case "QUESTION_UPDATED" -> {
                QuestionEventPayload question = objectMapper.treeToValue(data, QuestionEventPayload.class);
                indexQuestion(question);
            }

            case "QUESTION_DELETED" -> {
                QuestionDeletedEventPayload deletedEvent = objectMapper.treeToValue(data, QuestionDeletedEventPayload.class);
                deleteQuestion(deletedEvent);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported question event type: " + eventType
            );
        }
    }

    private void indexQuestion(QuestionEventPayload question) {
        QuestionSearchDocument document = QuestionSearchDocument.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUserId())
                .username(question.getUsername())
                .tags(question.getTags())
                .version(question.getVersion())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();

        try {
            questionSearchService.indexQuestion(document);
        } catch (ElasticsearchException exception) {
            if (exception.status() == 409) {
                log.info(
                        "Ignored stale question event. questionId={}, version={}",
                        question.getId(),
                        question.getVersion()
                );
                return;
            }

            throw exception;
        }
    }

    private void deleteQuestion(
            QuestionDeletedEventPayload deletedEvent
    ) {
        try {
            questionSearchService.deleteQuestion(
                    deletedEvent.id(),
                    deletedEvent.version()
            );
        } catch (ElasticsearchException exception) {
            if (exception.status() == 409) {
                log.info(
                        "Ignored stale delete event. questionId={}, version={}",
                        deletedEvent.id(),
                        deletedEvent.version()
                );
                return;
            }

            throw exception;
        }
    }
}
