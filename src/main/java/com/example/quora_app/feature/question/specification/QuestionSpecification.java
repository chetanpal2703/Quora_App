package com.example.quora_app.feature.question.specification;

import com.example.quora_app.feature.question.Question;
import org.springframework.data.jpa.domain.Specification;

public final class QuestionSpecification {

    private QuestionSpecification() {
    }

    public static Specification<Question> hasTag(String tagName) {

        return (root, query, criteriaBuilder) -> {

            if (tagName == null || tagName.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            query.distinct(true);

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(
                            root.join("tags").get("name")
                    ),
                    tagName.trim().toLowerCase()
            );
        };
    }

    public static Specification<Question> containsSearch(String search) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + search.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("content")),
                            pattern
                    )
            );
        };
    }
}