package com.example.quora_app.core.outbox.enums;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    FAILED
}
