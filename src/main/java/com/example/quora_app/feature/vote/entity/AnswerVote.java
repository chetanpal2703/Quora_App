package com.example.quora_app.feature.vote.entity;

import com.example.quora_app.core.common.entity.BaseEntity;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.vote.enums.VoteType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "answer_votes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_answer_vote_user_answer",
                        columnNames = {"user_id", "answer_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVote extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VoteType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;
}