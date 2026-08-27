CREATE TABLE answers (
                         id BINARY(16) NOT NULL,
                         created_at DATETIME(6) NOT NULL,
                         updated_at DATETIME(6) NOT NULL,
                         content TEXT NOT NULL,
                         user_id BINARY(16) NOT NULL,
                         question_id BINARY(16) NOT NULL,

                         PRIMARY KEY (id),

                         CONSTRAINT fk_answers_user
                             FOREIGN KEY (user_id)
                                 REFERENCES users(id),

                         CONSTRAINT fk_answers_question
                             FOREIGN KEY (question_id)
                                 REFERENCES questions(id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;