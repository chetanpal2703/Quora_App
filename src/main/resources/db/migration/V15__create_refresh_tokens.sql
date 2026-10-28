CREATE TABLE refresh_tokens (
                                id BINARY(16) NOT NULL,
                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6) NOT NULL,
                                version BIGINT NOT NULL DEFAULT 0,

                                user_id BINARY(16) NOT NULL,
                                token_hash VARCHAR(64) NOT NULL,
                                family_id BINARY(16) NOT NULL,
                                expires_at DATETIME(6) NOT NULL,
                                revoked_at DATETIME(6) NULL,
                                replaced_by_token_hash VARCHAR(64) NULL,

                                PRIMARY KEY (id),

                                CONSTRAINT uk_refresh_tokens_token_hash
                                    UNIQUE (token_hash),

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id)
                                        ON DELETE CASCADE,

                                INDEX idx_refresh_tokens_user_id (user_id),
                                INDEX idx_refresh_tokens_family_id (family_id),
                                INDEX idx_refresh_tokens_expires_at (expires_at)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;