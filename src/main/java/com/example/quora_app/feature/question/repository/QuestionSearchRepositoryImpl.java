package com.example.quora_app.feature.question.repository;

import com.example.quora_app.feature.question.Question;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class QuestionSearchRepositoryImpl implements QuestionSearchRepository {
    private final EntityManager entityManager;

    @Override
    public Page<Question> search(
            String search,
            String tag,
            Pageable pageable
    ) {

        // =========================================
        // 1. Find Question IDs using FULLTEXT
        // =========================================

        StringBuilder sql = new StringBuilder("""
                SELECT q.id
                FROM questions q
                LEFT JOIN question_tags qt
                    ON qt.question_id = q.id
                LEFT JOIN tags t
                    ON t.id = qt.tag_id
                WHERE MATCH(q.title, q.content)
                      AGAINST (:search IN NATURAL LANGUAGE MODE)
                """);

        if (tag != null && !tag.isBlank()) {
            sql.append("""
                    AND LOWER(t.name) = LOWER(:tag)
                    """);
        }

        sql.append("""
                GROUP BY q.id
                ORDER BY
                    MATCH(q.title, q.content)
                    AGAINST (:search IN NATURAL LANGUAGE MODE) DESC,
                    q.id
                """);

        Query query = entityManager.createNativeQuery(
                sql.toString()
        );

        query.setParameter("search", search);

        if (tag != null && !tag.isBlank()) {
            query.setParameter("tag", tag.trim());
        }

        query.setFirstResult(
                (int) pageable.getOffset()
        );

        query.setMaxResults(
                pageable.getPageSize()
        );

        List<?> rawIds = query.getResultList();

        // =========================================
        // 2. No results
        // =========================================

        if (rawIds.isEmpty()) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    count(search, tag)
            );
        }

        // =========================================
        // 3. Convert BINARY(16) → UUID
        // =========================================

        List<UUID> ids = rawIds.stream()
                .map(this::toUuid)
                .toList();

        // =========================================
        // 4. Fetch Questions + User + Tags
        // =========================================

        List<Question> questions =
                fetchQuestionsWithUserAndTags(ids);

        // =========================================
        // 5. Restore FULLTEXT relevance order
        // =========================================

        Map<UUID, Integer> order = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            order.put(ids.get(i), i);
        }

        questions.sort(
                Comparator.comparingInt(
                        question ->
                                order.get(question.getId())
                )
        );

        // =========================================
        // 6. Count total results
        // =========================================

        long total = count(search, tag);

        return new PageImpl<>(
                questions,
                pageable,
                total
        );
    }

    // =============================================
    // Fetch Questions + User + Tags
    // =============================================

    private List<Question> fetchQuestionsWithUserAndTags(
            List<UUID> ids
    ) {

        return entityManager.createQuery("""
                SELECT DISTINCT q
                FROM Question q
                LEFT JOIN FETCH q.user
                LEFT JOIN FETCH q.tags
                WHERE q.id IN :ids
                """, Question.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    // =============================================
    // Convert MySQL BINARY(16) → UUID
    // =============================================

    private UUID toUuid(Object value) {

        if (value instanceof UUID uuid) {
            return uuid;
        }

        if (value instanceof byte[] bytes) {

            ByteBuffer buffer =
                    ByteBuffer.wrap(bytes);

            long mostSignificantBits =
                    buffer.getLong();

            long leastSignificantBits =
                    buffer.getLong();

            return new UUID(
                    mostSignificantBits,
                    leastSignificantBits
            );
        }

        throw new IllegalArgumentException(
                "Unexpected UUID value type: "
                        + value.getClass()
        );
    }

    // =============================================
    // Count FULLTEXT results
    // =============================================

    private long count(
            String search,
            String tag
    ) {

        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT q.id)
                FROM questions q
                LEFT JOIN question_tags qt
                    ON qt.question_id = q.id
                LEFT JOIN tags t
                    ON t.id = qt.tag_id
                WHERE MATCH(q.title, q.content)
                      AGAINST (:search IN NATURAL LANGUAGE MODE)
                """);

        if (tag != null && !tag.isBlank()) {
            sql.append("""
                    AND LOWER(t.name) = LOWER(:tag)
                    """);
        }

        Query query = entityManager.createNativeQuery(
                sql.toString()
        );

        query.setParameter("search", search);

        if (tag != null && !tag.isBlank()) {
            query.setParameter("tag", tag.trim());
        }

        return ((Number) query.getSingleResult())
                .longValue();
    }

}

