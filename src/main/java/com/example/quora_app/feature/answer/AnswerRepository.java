package com.example.quora_app.feature.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    @EntityGraph(attributePaths = "user")
    Page<Answer> findByQuestionId(UUID questionId, Pageable pageable);
}
