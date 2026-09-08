package com.example.quora_app.feature.comment.mapper;


import com.example.quora_app.feature.comment.Comment;
import com.example.quora_app.feature.comment.dto.CommentResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponse toCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .username(comment.getUser().getUsername())
                .questionId(
                        comment.getQuestion() != null
                                ? comment.getQuestion().getId()
                                : null
                )
                .answerId(
                        comment.getAnswer() != null
                                ? comment.getAnswer().getId()
                                : null
                )
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
