package com.example.quora_app.feature.question.repository;

import com.example.quora_app.feature.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question>,QuestionSearchRepository {

    @EntityGraph(attributePaths = "user")
    @Override
    Page<Question> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title,
            String content,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "tags"})
    Optional<Question> findWithUserAndTagsById(UUID id);
}