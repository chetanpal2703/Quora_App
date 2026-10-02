package com.example.quora_app.core.outbox.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEventPayload<T> {
    private UUID eventId;

    private String eventType;

    private String aggregateType;

    private UUID aggregateId;

    private Instant occurredAt;

    private T data;
}
