package com.example.quora_app.feature.question;

import com.example.quora_app.core.common.entity.BaseEntity;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseEntity {
    @Column(nullable = false,length = 200)
    private String title;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers=new ArrayList<>();

}
