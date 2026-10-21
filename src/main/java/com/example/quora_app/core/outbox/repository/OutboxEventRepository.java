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
                    LIMIT 10
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxEvent> findPendingForUpdate();
//changed limit from 100 to 10
//What we're going to change
//    Your current configuration has:
//            - Batch size: 100 events per claim.
//- Publisher interval: 5 seconds.
//- Recovery interval: 60 seconds.
//- Recovery threshold: 5 minutes.
//- Kafka acknowledgment timeout: 10 seconds per event.
//    The problem is that the publisher sends events sequentially, while the recovery job considers any event stuck for more than five minutes eligible for recovery—even if the publisher is still working through its batch.


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
