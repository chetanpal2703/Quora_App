package com.example.quora_app.core.outbox.service;

import com.example.quora_app.core.outbox.entity.OutboxEvent;
import com.example.quora_app.core.outbox.enums.OutboxEventStatus;
import com.example.quora_app.core.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxTransactionService {
    private final OutboxEventRepository outboxEventRepository;
    @Transactional
    public List<OutboxEvent> claimEvents() {
        List<OutboxEvent> events = outboxEventRepository.findPendingForUpdate();
        LocalDateTime processingAt = LocalDateTime.now();
        for (OutboxEvent event : events) {
            event.setStatus(OutboxEventStatus.PROCESSING);
            event.setProcessingAt(processingAt);
        }
        return events;
    }

    @Transactional
    public void markProcessed(OutboxEvent event) {
        event.setStatus(OutboxEventStatus.PROCESSED);
        event.setProcessedAt(LocalDateTime.now());
        event.setProcessingAt(null);
        outboxEventRepository.save(event);
    }

    @Transactional
    public void markFailed(OutboxEvent event, Exception exception) {
        event.setRetryCount(event.getRetryCount() + 1);
        event.setLastError(exception.getMessage());
        event.setProcessingAt(null);
        if (event.getRetryCount() >= 5) {
            event.setStatus(OutboxEventStatus.FAILED);
        } else {
            event.setStatus(OutboxEventStatus.PENDING);
        }
        outboxEventRepository.save(event);
    }

    @Transactional
    public int recoverStuckEvents(LocalDateTime threshold) {
        return outboxEventRepository.recoverStuckEvents(OutboxEventStatus.PROCESSING, OutboxEventStatus.PENDING, threshold);
    }
}
