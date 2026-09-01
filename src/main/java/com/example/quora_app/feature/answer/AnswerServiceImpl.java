package com.example.quora_app.feature.answer;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.core.common.mapper.PageMapper;
import com.example.quora_app.core.exception.BadRequestException;
import com.example.quora_app.core.exception.ForbiddenException;
import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.core.security.CurrentUserService;
import com.example.quora_app.feature.answer.dto.AnswerCreateRequest;
import com.example.quora_app.feature.answer.dto.AnswerResponse;
import com.example.quora_app.feature.answer.dto.AnswerUpdateRequest;
import com.example.quora_app.feature.answer.mapper.AnswerMapper;
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

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;
    private final PageMapper pageMapper;
    private final CurrentUserService currentUserService;

    @Override
    public AnswerResponse createAnswer(AnswerCreateRequest request) {
        User user = userRepository.findById(currentUserService.getCurrentUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserService.getCurrentUserId()));

        Question question = questionRepository.findById(request.getQuestionId()).orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + request.getQuestionId()));

        Answer answer = Answer.builder()
                .content(request.getContent())
                .user(user)
                .question(question)
                .build();

        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }

    @Override
    public PageResponse<AnswerResponse> getAnswersByQuestion(UUID questionId, int page, int size, String sortBy, String sortDir) {
        if (page < 0) {
            throw new BadRequestException("Page cannot be negative");
        }

        if (size <= 0) {
            throw new BadRequestException("Size must be greater than 0");
        }

        if (size > 100) {
            throw new BadRequestException("Size cannot be greater than 100");
        }

        Set<String> allowedSortFields = Set.of("id", "createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            throw new BadRequestException("Invalid sort field: " + sortBy);
        }

        // First verify that the question actually exists.
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found with id: " + questionId);
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Answer> answerPage = answerRepository.findByQuestionId(questionId, pageable);

        return pageMapper.toPageResponse(
                answerPage,
                answerMapper::toResponse
        );
    }

    @Override
    public AnswerResponse getAnswerById(UUID id) {
        Answer answer =answerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
        return answerMapper.toResponse(answer);
    }

    @Override
    public AnswerResponse updateAnswer(UUID id, AnswerUpdateRequest request) {
        Answer answer=answerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));

//        UUID currentUserId = currentUserService.getCurrentUserId();

        currentUserService.verifyOwner(answer.getUser().getId(),"You are not allowed to update this answer");

//        if (!answer.getUser().getId().equals(currentUserId)) {
//            throw new ForbiddenException("You are not allowed to update this answer");
//        }
        if (answer.getContent() != null && answer.getContent().equals(request.getContent())) {
            return answerMapper.toResponse(answer);
        }
        answer.setContent(request.getContent());
        answer = answerRepository.save(answer);
        return answerMapper.toResponse(answer);
    }

    @Override
    public void deleteAnswer(UUID id) {
        Answer answer = answerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
//        UUID currentUserId = currentUserService.getCurrentUserId();

        currentUserService.verifyOwner(answer.getUser().getId(),"You are not allowed to delete this answer");
//        if (!answer.getUser().getId().equals(currentUserId)) {
//            throw new ForbiddenException("You are not allowed to delete this answer");
//        }
        answerRepository.delete(answer);
    }
}
