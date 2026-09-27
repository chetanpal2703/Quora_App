package com.example.quora_app.feature.question.search.dto;

import com.example.quora_app.feature.question.search.QuestionSearchDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSearchResult {

    private QuestionSearchDocument question;

    private Double score;
}
