package com.example.quora_app.feature.answer;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.answer.dto.AnswerCreateRequest;
import com.example.quora_app.feature.answer.dto.AnswerResponse;

import java.util.UUID;

public interface AnswerService {
    AnswerResponse createAnswer(AnswerCreateRequest request);
    PageResponse<AnswerResponse> getAnswersByQuestion(UUID questionId, int page, int size, String sortBy, String sortDir);
}
