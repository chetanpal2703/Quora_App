package com.example.quora_app.feature.user;

import com.example.quora_app.core.common.entity.BaseEntity;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.question.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true,length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Question> questions=new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Answer> answers=new ArrayList<>();

}
