package com.example.quora_app.feature.answer;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.answer.dto.AnswerCreateRequest;
import com.example.quora_app.feature.answer.dto.AnswerResponse;
import com.example.quora_app.feature.answer.dto.AnswerUpdateRequest;

import java.util.UUID;

public interface AnswerService {
    AnswerResponse createAnswer(AnswerCreateRequest request);
    PageResponse<AnswerResponse> getAnswersByQuestion(UUID questionId, int page, int size, String sortBy, String sortDir);
    AnswerResponse getAnswerById(UUID id);
    AnswerResponse updateAnswer(UUID id, AnswerUpdateRequest request);
    void deleteAnswer(UUID id);
}
