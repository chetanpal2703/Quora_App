package com.example.quora_app.feature.question;

import com.example.quora_app.core.common.entity.BaseEntity;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.comment.Comment;
import com.example.quora_app.feature.tag.Tag;
import com.example.quora_app.feature.user.User;
import com.example.quora_app.feature.vote.entity.QuestionVote;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Builder.Default
    private List<Answer> answers=new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<QuestionVote> votes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

}
