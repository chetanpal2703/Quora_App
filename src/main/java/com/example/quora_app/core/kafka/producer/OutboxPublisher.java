package com.example.quora_app.core.kafka.producer;

import com.example.quora_app.core.outbox.entity.OutboxEvent;
import com.example.quora_app.core.outbox.service.OutboxTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OutboxPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxTransactionService outboxTransactionService;
    private final String topic;

    public OutboxPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            OutboxTransactionService outboxTransactionService,
            @Value("${app.kafka.topics.question-events:question-events}")
            String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxTransactionService = outboxTransactionService;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publisher-delay-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxTransactionService.claimEvents();
        for (OutboxEvent event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEvent event) {
        try {
            String key = event.getAggregateId().toString();
            String payload = event.getPayload();

            // Wait for the Kafka broker's acknowledgment.
            kafkaTemplate.send(topic, key, payload)
                    .get(10, TimeUnit.SECONDS);

            // Update the Outbox only after the acknowledgment.
            outboxTransactionService.markPublished(event);

            log.info(
                    "Published outbox event {} of type {} to topic {}",
                    event.getId(),
                    event.getEventType(),
                    topic
            );

        } catch (Exception exception) {
            log.error(
                    "Failed to publish outbox event {}",
                    event.getId(),
                    exception
            );

            outboxTransactionService.markFailed(event, exception);
        }
    }
}
