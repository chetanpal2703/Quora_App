package com.example.quora_app.feature.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @EntityGraph(attributePaths = "user")
    Page<Comment> findByQuestionId(
            UUID questionId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "user")
    Page<Comment> findByAnswerId(
            UUID answerId,
            Pageable pageable
    );
}
