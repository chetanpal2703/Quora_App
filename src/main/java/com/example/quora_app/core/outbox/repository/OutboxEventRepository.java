package com.example.quora_app.core.outbox.repository;

import com.example.quora_app.core.outbox.entity.OutboxEvent;
import com.example.quora_app.core.outbox.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(
            value = """
                    SELECT *
                    FROM outbox_events
                    WHERE status = 'PENDING'
                    ORDER BY created_at
                    LIMIT 100
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxEvent> findPendingForUpdate();


    @Modifying
    @Query("""
            UPDATE OutboxEvent e
            SET e.status = :pending,
                e.processingAt = null
            WHERE e.status = :processing
              AND e.processingAt < :threshold
            """)
    int recoverStuckEvents(
            @Param("processing")
            OutboxEventStatus processing,

            @Param("pending")
            OutboxEventStatus pending,

            @Param("threshold")
            LocalDateTime threshold
    );
}
