package com.example.quora_app.core.kafka.consumer;

import com.example.quora_app.feature.question.search.QuestionEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionEventConsumer {

    private final QuestionEventHandler questionEventHandler;

    @KafkaListener(
            topics = "${app.kafka.topics.question-events:question-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) throws Exception {
        log.info("Received question event from Kafka");

        // If handling fails, let the exception propagate so that
        // Kafka's listener error handling can retry the record.
        questionEventHandler.handle(message);

        log.info("Successfully processed question event");
    }
}
