package com.example.quora_app.feature.comment;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.core.common.mapper.PageMapper;
import com.example.quora_app.core.exception.ForbiddenException;
import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.core.security.CurrentUserService;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.answer.AnswerRepository;
import com.example.quora_app.feature.comment.dto.CommentCreateRequest;
import com.example.quora_app.feature.comment.dto.CommentResponse;
import com.example.quora_app.feature.comment.dto.CommentUpdateRequest;
import com.example.quora_app.feature.comment.mapper.CommentMapper;
import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.QuestionRepository;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public CommentResponse createQuestionComment(UUID questionId, CommentCreateRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        User user=userRepository.findById(currentUserId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        Question question = questionRepository.findById(questionId).orElseThrow(()->new ResourceNotFoundException("Question not found with id: " + questionId));
        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .question(question)
                .build();
        Comment savedComment=commentRepository.save(comment);
        return commentMapper.toCommentResponse(savedComment);
    }

    @Override
    @Transactional
    public CommentResponse createAnswerComment(UUID answerId, CommentCreateRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .answer(answer)
                .build();
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getQuestionComments(
            UUID questionId,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> comments = commentRepository.findByQuestionId(questionId, pageable);
        return pageMapper.toPageResponse(comments, commentMapper::toCommentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getAnswerComments(
            UUID answerId,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        answerRepository.findById(answerId).orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> comments = commentRepository.findByAnswerId(answerId, pageable);
        return pageMapper.toPageResponse(comments, commentMapper::toCommentResponse);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID commentId, CommentUpdateRequest request) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not allowed to update this comment");
        }
        comment.setContent(request.getContent());
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId) {
        UUID currentUserId = currentUserService.getCurrentUserId();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not allowed to delete this comment");
        }
        commentRepository.delete(comment);
    }
}
