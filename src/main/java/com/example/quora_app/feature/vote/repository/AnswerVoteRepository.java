package com.example.quora_app.feature.vote.repository;

import com.example.quora_app.feature.vote.entity.AnswerVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote, UUID> {
    Optional<AnswerVote> findByUserIdAndAnswerId(UUID userId, UUID answerId);
}
