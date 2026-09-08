package com.example.quora_app.feature.comment;

import com.example.quora_app.feature.comment.dto.CommentCreateRequest;
import com.example.quora_app.feature.comment.dto.CommentResponse;

import java.util.UUID;

public interface CommentService {
    CommentResponse createQuestionComment(UUID questionId, CommentCreateRequest request);
    CommentResponse createAnswerComment(UUID answerId, CommentCreateRequest request);
}
