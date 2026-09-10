package com.example.quora_app.feature.vote;

import com.example.quora_app.feature.vote.dto.VoteRequest;

import java.util.UUID;

public interface VoteService {
    void voteOnQuestion(UUID questionId, VoteRequest request);

    void voteOnAnswer(UUID answerId, VoteRequest request);

    void removeVoteFromQuestion(UUID questionId);

    void removeVoteFromAnswer(UUID answerId);
}
