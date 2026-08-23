package com.example.quora_app.feature.question;

import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import com.example.quora_app.feature.question.mapper.QuestionMapper;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;

    @Override
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        User user=userRepository.findById(request.getUserId()).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+request.getUserId()));
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
}
