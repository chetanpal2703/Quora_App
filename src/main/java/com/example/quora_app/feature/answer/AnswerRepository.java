package com.example.quora_app.feature.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface AnswerRepository extends JpaRepository<Answer, UUID> {
}
