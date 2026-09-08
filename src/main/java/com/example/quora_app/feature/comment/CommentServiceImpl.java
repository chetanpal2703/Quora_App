package com.example.quora_app.feature.comment;

import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.core.security.CurrentUserService;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.answer.AnswerRepository;
import com.example.quora_app.feature.comment.dto.CommentCreateRequest;
import com.example.quora_app.feature.comment.dto.CommentResponse;
import com.example.quora_app.feature.comment.mapper.CommentMapper;
import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.QuestionRepository;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
