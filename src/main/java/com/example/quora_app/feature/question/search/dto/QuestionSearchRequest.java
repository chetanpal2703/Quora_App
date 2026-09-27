package com.example.quora_app.feature.question.search.dto;

import com.example.quora_app.feature.question.enums.QuestionSearchSort;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSearchRequest {
    private String search;

    private String tag;

    private LocalDate fromDate;

    private LocalDate toDate;

    @Min(0)
    @Builder.Default
    private int page = 0;

    @Min(1)
    @Max(100)
    @Builder.Default
    private int size = 10;

    @Builder.Default
    private QuestionSearchSort sort = QuestionSearchSort.RELEVANCE;
}
