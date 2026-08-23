package com.example.quora_app.feature.question;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Validated
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@RequestBody QuestionCreateRequest request){
        QuestionResponse questionResponse=questionService.createQuestion(request);
        ApiResponse<QuestionResponse> response= ApiResponse.<QuestionResponse>builder()
                .success(true)
                .data(questionResponse)
                .message("Question created successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(@Valid @PathVariable UUID id){
        QuestionResponse question=questionService.getQuestionById(id);
        ApiResponse<QuestionResponse> response= ApiResponse.<QuestionResponse>builder()
                .success(true)
                .data(question)
                .message("Question found with id: "+id)
                .build();
       return ResponseEntity.ok(response);
    }
}
