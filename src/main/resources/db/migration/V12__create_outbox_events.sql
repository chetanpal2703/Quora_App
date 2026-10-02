CREATE TABLE outbox_events (
                               id BINARY(16) NOT NULL,

                               aggregate_type VARCHAR(100) NOT NULL,

                               aggregate_id BINARY(16) NOT NULL,

                               event_type VARCHAR(100) NOT NULL,

                               payload JSON NOT NULL,

                               status VARCHAR(30) NOT NULL,

                               created_at DATETIME(6) NOT NULL,

                               processing_at DATETIME(6) NULL,

                               processed_at DATETIME(6) NULL,

                               retry_count INT NOT NULL DEFAULT 0,

                               last_error TEXT NULL,

                               PRIMARY KEY (id),

                               INDEX idx_outbox_status_created_at (
        status,
        created_at
    ),

                               INDEX idx_outbox_aggregate (
        aggregate_type,
        aggregate_id
    )
)
    ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;