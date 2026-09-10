package com.example.quora_app.feature.vote.repository;

import com.example.quora_app.feature.vote.entity.QuestionVote;
import com.example.quora_app.feature.vote.enums.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuestionVoteRepository extends JpaRepository<QuestionVote, UUID> {

    Optional<QuestionVote> findByUserIdAndQuestionId(UUID userId, UUID questionId);

    long countByQuestionIdAndType(UUID questionId, VoteType type);

}
