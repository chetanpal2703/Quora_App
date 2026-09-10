package com.example.quora_app.feature.vote;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.vote.dto.VoteRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class VoteController {

    private final VoteService voteService;

    @PreAuthorize("hasAuthority('VOTE_CREATE')")
    @PutMapping("/questions/{questionId}/vote")
    public ResponseEntity<ApiResponse<Void>> voteOnQuestion(@PathVariable UUID questionId, @Valid @RequestBody VoteRequest request) {
        voteService.voteOnQuestion(questionId, request);
        ApiResponse<Void> response=ApiResponse.<Void>builder()
                .success(true)
                .message("Successfully added vote on question")
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('VOTE_CREATE')")
    @PutMapping("/answers/{answerId}/vote")
    public ResponseEntity<ApiResponse<Void>> voteOnAnswer(@PathVariable UUID answerId, @Valid @RequestBody VoteRequest request) {
        voteService.voteOnAnswer(answerId, request);
        ApiResponse<Void> response=ApiResponse.<Void>builder()
                .success(true)
                .message("Successfully added Vote on Answer")
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('VOTE_DELETE')")
    @DeleteMapping("/questions/{questionId}/vote")
    public ResponseEntity<Void> removeQuestionVote(@PathVariable UUID questionId) {
        voteService.removeVoteFromQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('VOTE_DELETE')")
    @DeleteMapping("/answers/{answerId}/vote")
    public ResponseEntity<Void> removeAnswerVote(@PathVariable UUID answerId) {
        voteService.removeVoteFromAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
}
