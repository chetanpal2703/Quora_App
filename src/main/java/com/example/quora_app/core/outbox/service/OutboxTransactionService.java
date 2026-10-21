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
        OutboxEvent managedEvent = outboxEventRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Outbox event not found: " + event.getId()
                ));

        int retryCount = managedEvent.getRetryCount() + 1;

        managedEvent.setRetryCount(retryCount);
        managedEvent.setLastError(exception.getMessage());
        managedEvent.setProcessingAt(null);

        if (retryCount >= 5) {
            managedEvent.setStatus(OutboxEventStatus.FAILED);
        } else {
            managedEvent.setStatus(OutboxEventStatus.PENDING);
        }
    }

    @Transactional
    public int recoverStuckEvents(LocalDateTime threshold) {
        return outboxEventRepository.recoverStuckEvents(OutboxEventStatus.PROCESSING, OutboxEventStatus.PENDING, threshold);
    }

    @Transactional
    public void markPublished(OutboxEvent event) {
        OutboxEvent managedEvent = outboxEventRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Outbox event not found: " + event.getId()
                ));

        managedEvent.setStatus(OutboxEventStatus.PUBLISHED);
        managedEvent.setProcessingAt(null);
        managedEvent.setProcessedAt(null);
        managedEvent.setLastError(null);
//      As we are inside transaction so dirty checking happen and save automatically
//        outboxEventRepository.save(managedEvent);
    }
}
