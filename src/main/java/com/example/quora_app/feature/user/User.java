package com.example.quora_app.feature.user;

import com.example.quora_app.core.common.entity.BaseEntity;
import com.example.quora_app.feature.answer.Answer;
import com.example.quora_app.feature.authorization.entity.Role;
import com.example.quora_app.feature.question.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Question> questions=new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Answer> answers=new ArrayList<>();

}
