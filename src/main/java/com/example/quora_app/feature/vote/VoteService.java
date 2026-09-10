package com.example.quora_app.feature.vote;

import com.example.quora_app.feature.vote.dto.VoteRequest;
import com.example.quora_app.feature.vote.dto.VoteSummaryResponse;

import java.util.UUID;

public interface VoteService {
    void voteOnQuestion(UUID questionId, VoteRequest request);

    void voteOnAnswer(UUID answerId, VoteRequest request);

    void removeVoteFromQuestion(UUID questionId);

    void removeVoteFromAnswer(UUID answerId);

    VoteSummaryResponse getQuestionVoteSummary(UUID questionId);

    VoteSummaryResponse getAnswerVoteSummary(UUID answerId);
}
