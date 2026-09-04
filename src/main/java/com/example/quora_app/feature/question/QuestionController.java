package com.example.quora_app.feature.question;

import com.example.quora_app.core.common.dto.ApiResponse;
import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.feature.answer.AnswerService;
import com.example.quora_app.feature.answer.dto.AnswerResponse;
import com.example.quora_app.feature.question.dto.QuestionCreateRequest;
import com.example.quora_app.feature.question.dto.QuestionResponse;
import com.example.quora_app.feature.question.dto.QuestionUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Validated
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;

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

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponse>>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search
    ) {
        PageResponse<QuestionResponse> questions = questionService.getAllQuestions(page, size, sortBy, sortDir,search);

        ApiResponse<PageResponse<QuestionResponse>> response =
                ApiResponse.<PageResponse<QuestionResponse>>builder()
                        .success(true)
                        .message("Questions fetched successfully")
                        .data(questions)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(@PathVariable UUID id,@Valid @RequestBody QuestionUpdateRequest request){
        QuestionResponse questionResponse=questionService.updateQuestion(id, request);
        ApiResponse<QuestionResponse> response=ApiResponse.<QuestionResponse>builder()
                .success(true)
                .message("Question updated Successfully")
                .data(questionResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('QUESTION_DELETE')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{questionId}/answer")
    public ResponseEntity<ApiResponse<PageResponse<AnswerResponse>>> getAnswersByQuestion(
            @PathVariable UUID questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PageResponse<AnswerResponse> answers = answerService.getAnswersByQuestion(questionId, page, size, sortBy, sortDir);

        ApiResponse<PageResponse<AnswerResponse>> response = ApiResponse.<PageResponse<AnswerResponse>>builder()
                .success(true)
                .message("Answers fetched successfully")
                .data(answers)
                .build();

        return ResponseEntity.ok(response);
    }
}
