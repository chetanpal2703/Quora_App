CREATE TABLE questions (
                           id BINARY(16) NOT NULL,
                           created_at DATETIME(6) NOT NULL,
                           updated_at DATETIME(6) NOT NULL,
                           title VARCHAR(200) NOT NULL,
                           content TEXT NOT NULL,
                           user_id BINARY(16) NOT NULL,

                           PRIMARY KEY (id),

                           CONSTRAINT fk_questions_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;