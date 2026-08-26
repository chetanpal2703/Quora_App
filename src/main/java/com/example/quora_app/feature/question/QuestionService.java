package com.example.quora_app.feature.question;


import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import com.example.quora_app.feature.question.dto.QuestionUpdateRequest;

import java.util.UUID;

public interface QuestionService {
    QuestionResponse createQuestion(QuestionCreateRequest request);

    QuestionResponse getQuestionById(UUID id);

//    PageResponse<QuestionResponse> getAllQuestions(int page, int size, String sortBy, String sortDir);

    PageResponse<QuestionResponse> getAllQuestions(int page, int size, String sortBy, String sortDir, String search);

    QuestionResponse updateQuestion(UUID id, QuestionUpdateRequest request);

    void deleteQuestion(UUID id);
}
