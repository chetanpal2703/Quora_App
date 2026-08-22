package com.example.quora_app.feature.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

}
