package com.example.quora_app.feature.question;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.core.common.mapper.PageMapper;
import com.example.quora_app.core.exception.BadRequestException;
import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.core.security.CurrentUserService;
import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import com.example.quora_app.feature.question.dto.QuestionUpdateRequest;
import com.example.quora_app.feature.question.mapper.QuestionMapper;
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
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;
    private final PageMapper pageMapper;
    private final CurrentUserService currentUserService;

    @Override
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        User user=userRepository.findById(currentUserService.getCurrentUserId()).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+currentUserService.getCurrentUserId()));
        Question question= Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
        Question savedQuestion= questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public QuestionResponse getQuestionById(UUID id) {
        Question question= questionRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Question not found with id: "+id));
        return questionMapper.toResponse(question);
    }

    @Override
    public PageResponse<QuestionResponse> getAllQuestions(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search
    ) {

        if (page < 0) {
            throw new BadRequestException("Page cannot be negative");
        }

        if (size <= 0) {
            throw new BadRequestException("Size must be greater than 0");
        }

        if (size > 100) {
            throw new BadRequestException("Size cannot be greater than 100");
        }

        Set<String> allowedSortFields = Set.of("id", "title", "createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortBy)
        );

        Page<Question> questionPage;

        if (search == null || search.isBlank()) {

            questionPage = questionRepository.findAll(pageable);

        } else {

            String keyword = search.trim();

            questionPage =
                    questionRepository
                            .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                                    keyword,
                                    keyword,
                                    pageable
                            );
        }

        return pageMapper.toPageResponse(
                questionPage,
                questionMapper::toResponse
        );
    }

    @Override
    public QuestionResponse updateQuestion(UUID id, QuestionUpdateRequest request) {
        Question question =questionRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Question not found with id: "+id));
        if (request.getTitle() != null && !request.getTitle().equals(question.getTitle())) {
            question.setTitle(request.getTitle());
        }
        if (request.getContent() != null && !request.getContent().equals(question.getContent())) {
            question.setContent(request.getContent());
        }
        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public void deleteQuestion(UUID id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        questionRepository.delete(question);
    }


}
