package com.example.quora_app.feature.question;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.core.common.mapper.PageMapper;
import com.example.quora_app.core.exception.BadRequestException;
import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.core.security.CurrentUserService;
import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import com.example.quora_app.feature.question.dto.QuestionUpdateRequest;
import com.example.quora_app.feature.question.event.QuestionCreatedEvent;
import com.example.quora_app.feature.question.event.QuestionDeletedEvent;
import com.example.quora_app.feature.question.event.QuestionUpdatedEvent;
import com.example.quora_app.feature.question.mapper.QuestionMapper;
import com.example.quora_app.feature.question.repository.QuestionRepository;
import com.example.quora_app.feature.question.specification.QuestionSpecification;
import com.example.quora_app.feature.tag.Tag;
import com.example.quora_app.feature.tag.TagRepository;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final TagRepository tagRepository;
    private final QuestionMapper questionMapper;
    private final PageMapper pageMapper;
    private final ApplicationEventPublisher eventPublisher;

    private Set<Tag> resolveTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        return tagNames.stream()
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .map(String::toLowerCase)
                .map(this::findOrCreateTag)
                .collect(Collectors.toSet());
    }

    private Tag findOrCreateTag(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(
                        () -> tagRepository.save(Tag.builder().name(name).build())
                );
    }

    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        User user=userRepository.findById(currentUserService.getCurrentUserId()).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+currentUserService.getCurrentUserId()));
        Set<Tag> tags = resolveTags(request.getTags());
        Question question= Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .tags(tags)
                .build();
        Question savedQuestion= questionRepository.save(question);
        eventPublisher.publishEvent(new QuestionCreatedEvent(savedQuestion.getId()));
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public QuestionResponse getQuestionById(UUID id) {
        Question question= questionRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Question not found with id: "+id));
        return questionMapper.toResponse(question);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<QuestionResponse> getAllQuestions(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search,
            String tag
    ) {

        // -----------------------------------------
        // Pagination validation
        // -----------------------------------------

        if (page < 0) {
            throw new BadRequestException("Page cannot be negative");
        }

        if (size <= 0) {
            throw new BadRequestException("Size must be greater than 0");
        }

        if (size > 100) {
            throw new BadRequestException("Size cannot be greater than 100");
        }

        Page<Question> questionPage;

        // =========================================
        // FULLTEXT SEARCH
        // =========================================

        if (search != null && !search.isBlank()) {
            /*
             * FULLTEXT search controls its own ordering
             * using relevance DESC.
             *
             * Therefore sortBy and sortDir are ignored.
             */
            Pageable pageable = PageRequest.of(page, size);

            questionPage = questionRepository.search(search.trim(), tag, pageable);
        }

        // =========================================
        // NORMAL LISTING / FILTERING
        // =========================================

        else {

            Set<String> allowedSortFields = Set.of("id", "title", "createdAt", "updatedAt");

            if (!allowedSortFields.contains(sortBy)) {
                throw new BadRequestException("Invalid sort field: " + sortBy);
            }

            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Specification<Question> specification = Specification.where(QuestionSpecification.hasTag(tag));

            questionPage = questionRepository.findAll(specification, pageable);
        }

        return pageMapper.toPageResponse(
                questionPage,
                questionMapper::toResponse
        );
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(UUID id, QuestionUpdateRequest request) {
        Question question =questionRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Question not found with id: "+id));
//        UUID currentUserId = currentUserService.getCurrentUserId();
//        if (!question.getUser().getId().equals(currentUserId)) {
//            throw new ForbiddenException("You are not allowed to update this question");
//        }
        currentUserService.verifyOwner(question.getUser().getId(), "You are not allowed to update this question");
        if (request.getTitle() != null && !request.getTitle().equals(question.getTitle())) {
            question.setTitle(request.getTitle());
        }
        if (request.getContent() != null && !request.getContent().equals(question.getContent())) {
            question.setContent(request.getContent());
        }
        if (request.getTags() != null) {
            question.setTags(resolveTags(request.getTags()));
        }
        Question updatedQuestion = questionRepository.save(question);
        eventPublisher.publishEvent(new QuestionUpdatedEvent(updatedQuestion.getId()));
        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
//        UUID currentUserId = currentUserService.getCurrentUserId();
//        if (!question.getUser().getId().equals(currentUserId)) {
//            throw new ForbiddenException("You are not allowed to delete this question");
//        }
        currentUserService.verifyOwner(question.getUser().getId(), "You are not allowed to delete this question");
        questionRepository.delete(question);
        eventPublisher.publishEvent(new QuestionDeletedEvent(id));
    }


}
