package com.example.quora_app.core.exception;

public class OutboxSerializationException extends RuntimeException {
    public OutboxSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
