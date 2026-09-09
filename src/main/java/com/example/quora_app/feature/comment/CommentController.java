package com.example.quora_app.feature.comment;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.comment.dto.CommentCreateRequest;
import com.example.quora_app.feature.comment.dto.CommentResponse;
import com.example.quora_app.feature.comment.dto.CommentUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/questions/{questionId}/comments")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getQuestionComments(
            @PathVariable UUID questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        PageResponse<CommentResponse> response = commentService.getQuestionComments(questionId, page, size, sortBy, sortDir);
        ApiResponse<PageResponse<CommentResponse>> apiResponse= ApiResponse.<PageResponse<CommentResponse>>builder()
                .success(true)
                .message("Question Comment found successfully")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/answers/{answerId}/comments")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getAnswerComments(
            @PathVariable UUID answerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<CommentResponse> response = commentService.getAnswerComments(answerId, page, size, sortBy, sortDir);
        ApiResponse<PageResponse<CommentResponse>> apiResponse= ApiResponse.<PageResponse<CommentResponse>>builder()
                .success(true)
                .message("Answer Comment found successfully")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAuthority('COMMENT_UPDATE')")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable UUID commentId, @Valid @RequestBody CommentUpdateRequest request) {
        CommentResponse response = commentService.updateComment(commentId, request);
        ApiResponse<CommentResponse> apiResponse= ApiResponse.<CommentResponse>builder()
                .success(true)
                .message("Comment updated successfully")
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAuthority('COMMENT_DELETE')")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
