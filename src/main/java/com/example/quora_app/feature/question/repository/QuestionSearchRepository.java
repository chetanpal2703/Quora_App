package com.example.quora_app.feature.question.repository;

import com.example.quora_app.feature.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface
QuestionSearchRepository {
    Page<Question> search(String search, String tag, Pageable pageable);
}
