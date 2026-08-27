package com.example.quora_app.feature.answer;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.answer.dto.AnswerCreateRequest;
import com.example.quora_app.feature.answer.dto.AnswerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(@Valid @RequestBody AnswerCreateRequest request) {
        AnswerResponse answerResponse = answerService.createAnswer(request);

        ApiResponse<AnswerResponse> response =
                ApiResponse.<AnswerResponse>builder()
                        .success(true)
                        .message("Answer created successfully")
                        .data(answerResponse)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
