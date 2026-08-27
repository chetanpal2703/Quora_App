package com.example.quora_app.feature.answer;

import com.example.quora_app.core.common.entity.BaseEntity;
import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer extends BaseEntity {
    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

}
