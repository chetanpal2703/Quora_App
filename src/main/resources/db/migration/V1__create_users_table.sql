CREATE TABLE users (
                       id BINARY(16) NOT NULL,
                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,
                       email VARCHAR(50) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       username VARCHAR(50) NOT NULL,

                       PRIMARY KEY (id),

                       UNIQUE KEY uk_users_email (email),
                       UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;