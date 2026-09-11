package com.example.quora_app.feature.tag;

import com.example.quora_app.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
