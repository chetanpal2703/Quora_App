package com.example.quora_app.feature.question;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@Valid @RequestBody QuestionCreateRequest request){
        QuestionResponse questionResponse=questionService.createQuestion(request);
        ApiResponse<QuestionResponse> response= ApiResponse.<QuestionResponse>builder()
                .success(true)
                .data(questionResponse)
                .message("Question created successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
