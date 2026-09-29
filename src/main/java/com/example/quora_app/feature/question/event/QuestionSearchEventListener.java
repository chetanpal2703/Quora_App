package com.example.quora_app.feature.question.event;

import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.repository.QuestionRepository;
import com.example.quora_app.feature.question.search.QuestionSearchDocument;
import com.example.quora_app.feature.question.search.QuestionSearchService;
import com.example.quora_app.feature.question.search.mapper.QuestionSearchDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionSearchEventListener {
    private final QuestionRepository questionRepository;
    private final QuestionSearchDocumentMapper questionSearchDocumentMapper;
    private final QuestionSearchService questionSearchService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQuestionCreated(QuestionCreatedEvent event) {
        indexQuestion(event.questionId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQuestionUpdated(QuestionUpdatedEvent event) {
        indexQuestion(event.questionId());
    }

    private void indexQuestion(UUID questionId) {
        try {
            Question question = questionRepository.findWithUserAndTagsById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));

            QuestionSearchDocument document = questionSearchDocumentMapper.toDocument(question);
            questionSearchService.indexQuestion(document);
        } catch (Exception e) {
            // Logs the error to your console/file but prevents the app from crashing
            log.error("Failed to sync question to Elasticsearch. Question ID: {}", questionId, e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQuestionDeleted(QuestionDeletedEvent event) {
        try {
            // No fetching from MySQL needed. Just tell ES to delete the ID.
            questionSearchService.deleteQuestion(event.questionId());
        } catch (Exception e) {
            log.error("Failed to delete question from Elasticsearch. Question ID: {}", event.questionId(), e);
        }
    }


}
