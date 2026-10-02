package com.example.quora_app.core.outbox.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.quora_app.core.outbox.entity.OutboxEvent;
import com.example.quora_app.core.outbox.event.OutboxEventPayload;
import com.example.quora_app.feature.question.event.QuestionDeletedEventPayload;
import com.example.quora_app.feature.question.event.QuestionEventPayload;
import com.example.quora_app.feature.question.search.QuestionSearchDocument;
import com.example.quora_app.feature.question.search.QuestionSearchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {
    private final OutboxTransactionService outboxTransactionService;
    private final QuestionSearchService questionSearchService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void processEvents() {
        List<OutboxEvent> events = outboxTransactionService.claimEvents();
        for (OutboxEvent event : events) {
            try {
                processEvent(event);
                outboxTransactionService.markProcessed(event);
            } catch (Exception exception) {
                outboxTransactionService.markFailed(event, exception);
            }
        }
    }

    private void processEvent(OutboxEvent event) throws Exception {
        switch (event.getEventType()) {
            case QUESTION_CREATED -> handleQuestionCreated(event);
            case QUESTION_UPDATED -> handleQuestionUpdated(event);
            case QUESTION_DELETED -> handleQuestionDeleted(event);
            default -> throw new IllegalStateException("Unsupported event type: " + event.getEventType());
        }
    }

    private void handleQuestionCreated(OutboxEvent event) throws Exception {
        OutboxEventPayload<QuestionEventPayload> payload = objectMapper.readValue(event.getPayload(), new TypeReference<>() {});
        indexQuestion(payload.getData());
    }

    private void handleQuestionUpdated(OutboxEvent event) throws Exception {
        OutboxEventPayload<QuestionEventPayload> payload = objectMapper.readValue(event.getPayload(), new TypeReference<>() {});
        indexQuestion(payload.getData());
    }

    private void handleQuestionDeleted(OutboxEvent event) throws Exception {
        OutboxEventPayload<QuestionDeletedEventPayload> payload = objectMapper.readValue(event.getPayload(), new TypeReference<>() {});
        try {
            questionSearchService.deleteQuestion(payload.getData().id(), payload.getData().version());
        } catch (ElasticsearchException e) {
            if (e.status() == 409) {
                log.info("Ignored stale delete event. questionId={}, version={}", payload.getData().id(), payload.getData().version());
                return;
            }
            throw e;
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
        } catch (ElasticsearchException e) {
            if (e.status() == 409) {
                log.info("Ignored stale Question event. questionId={}, version={}. " + "Elasticsearch already contains a newer version.", question.getId(), question.getVersion());
                return;
            }
            throw e;
        }

    }

    @Scheduled(fixedDelay = 60_000)
    public void recoverStuckEvents() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        outboxTransactionService.recoverStuckEvents(threshold);
    }
}
