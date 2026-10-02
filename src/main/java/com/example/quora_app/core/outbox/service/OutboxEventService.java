package com.example.quora_app.core.outbox.service;

import com.example.quora_app.core.exception.OutboxSerializationException;
import com.example.quora_app.core.outbox.entity.OutboxEvent;
import com.example.quora_app.core.outbox.enums.OutboxEventStatus;
import com.example.quora_app.core.outbox.enums.OutboxEventType;
import com.example.quora_app.core.outbox.event.OutboxEventPayload;
import com.example.quora_app.core.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    public <T> void saveEvent(OutboxEventType eventType, String aggregateType, UUID aggregateId, T data) {

        UUID eventId = UUID.randomUUID();

        OutboxEventPayload<T> eventPayload =
                OutboxEventPayload.<T>builder()
                        .eventId(eventId)
                        .eventType(eventType.name())
                        .aggregateType(aggregateType)
                        .aggregateId(aggregateId)
                        .occurredAt(Instant.now())
                        .data(data)
                        .build();

        String payload = serialize(eventPayload);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(eventId)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .status(OutboxEventStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .retryCount(0)
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);

        } catch (JsonProcessingException e) {

            throw new OutboxSerializationException(
                    "Failed to serialize outbox event",
                    e
            );
        }
    }
}
