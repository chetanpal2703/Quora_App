package com.example.quora_app.core.outbox.entity;

import com.example.quora_app.core.outbox.enums.OutboxEventStatus;
import com.example.quora_app.core.outbox.enums.OutboxEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(
                        name = "idx_outbox_status_created_at",
                        columnList = "status, created_at"
                ),
                @Index(
                        name = "idx_outbox_aggregate",
                        columnList = "aggregate_type, aggregate_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private OutboxEventType eventType;

    @Column(nullable = false, columnDefinition = "JSON")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OutboxEventStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processingAt;

    private LocalDateTime processedAt;

    @Column(nullable = false)
    private int retryCount;

    @Column(columnDefinition = "TEXT")
    private String lastError;
}
