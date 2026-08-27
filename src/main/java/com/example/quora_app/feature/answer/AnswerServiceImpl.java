package com.example.quora_app.feature.answer;

import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.feature.answer.dto.AnswerCreateRequest;
import com.example.quora_app.feature.answer.dto.AnswerResponse;
import com.example.quora_app.feature.answer.mapper.AnswerMapper;
import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.question.QuestionRepository;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;

    @Override
    public AnswerResponse createAnswer(AnswerCreateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Question question = questionRepository.findById(request.getQuestionId()).orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + request.getQuestionId()));

        Answer answer = Answer.builder()
                .content(request.getContent())
                .user(user)
                .question(question)
                .build();

        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }
}
