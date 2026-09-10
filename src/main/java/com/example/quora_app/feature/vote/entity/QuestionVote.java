package com.example.quora_app.feature.vote.entity;

import com.example.quora_app.core.common.entity.BaseEntity;
import com.example.quora_app.feature.question.Question;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.vote.enums.VoteType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "question_votes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_question_vote_user_question",
                        columnNames = {"user_id", "question_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionVote extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VoteType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}