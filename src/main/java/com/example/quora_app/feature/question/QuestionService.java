package com.example.quora_app.feature.question;


import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;

import java.util.UUID;

public interface QuestionService {
    QuestionResponse createQuestion(QuestionCreateRequest request);

    QuestionResponse getQuestionById(UUID id);
}
