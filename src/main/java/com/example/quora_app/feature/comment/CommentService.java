package com.example.quora_app.feature.comment;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.comment.dto.CommentCreateRequest;
import com.example.quora_app.feature.comment.dto.CommentResponse;
import com.example.quora_app.feature.comment.dto.CommentUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    CommentResponse createQuestionComment(UUID questionId, CommentCreateRequest request);
    CommentResponse createAnswerComment(UUID answerId, CommentCreateRequest request);
    PageResponse<CommentResponse> getQuestionComments(UUID questionId, int page, int size, String sortBy, String sortDir);
    PageResponse<CommentResponse> getAnswerComments(UUID answerId, int page, int size, String sortBy, String sortDir);

    CommentResponse updateComment(UUID commentId, CommentUpdateRequest request);
    void deleteComment(UUID commentId);
}
