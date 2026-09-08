CREATE TABLE comments (
                          id BINARY(16) NOT NULL,
                          created_at DATETIME(6) NOT NULL,
                          updated_at DATETIME(6) NOT NULL,
                          content TEXT NOT NULL,

                          user_id BINARY(16) NOT NULL,
                          question_id BINARY(16) NULL,
                          answer_id BINARY(16) NULL,

                          PRIMARY KEY (id),

                          CONSTRAINT fk_comments_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id),

                          CONSTRAINT fk_comments_question
                              FOREIGN KEY (question_id)
                                  REFERENCES questions(id),

                          CONSTRAINT fk_comments_answer
                              FOREIGN KEY (answer_id)
                                  REFERENCES answers(id),

                          CONSTRAINT chk_comments_single_parent
                              CHECK (
                                  (question_id IS NOT NULL AND answer_id IS NULL)
                                      OR
                                  (question_id IS NULL AND answer_id IS NOT NULL)
                                  )
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;