package com.example.quora_app.feature.comment;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.comment.dto.CommentCreateRequest;
import com.example.quora_app.feature.comment.dto.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@Validated
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    @PostMapping("/questions/{questionId}/comments")
    ResponseEntity<ApiResponse<CommentResponse>> createQuestionComment(@PathVariable UUID questionId, @Valid @RequestBody CommentCreateRequest request){
        CommentResponse commentResponse = commentService.createQuestionComment(questionId, request);
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .success(true)
                .message("Question Comment created successfully")
                .data(commentResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    @PostMapping("/answers/{answerId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createAnswerComment(@PathVariable UUID answerId, @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse response = commentService.createAnswerComment(answerId, request);
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .success(true)
                .message("Answer Comment created successfully")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
