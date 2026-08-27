package com.example.quora_app.feature.answer;

import com.example.quora_app.feature.answer.dto.AnswerCreateRequest;
import com.example.quora_app.feature.answer.dto.AnswerResponse;

public interface AnswerService {
    AnswerResponse createAnswer(AnswerCreateRequest request);
}
