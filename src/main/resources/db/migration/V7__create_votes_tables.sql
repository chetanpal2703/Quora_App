CREATE TABLE question_votes (
                                id BINARY(16) NOT NULL,
                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6) NOT NULL,
                                type VARCHAR(20) NOT NULL,

                                user_id BINARY(16) NOT NULL,
                                question_id BINARY(16) NOT NULL,

                                PRIMARY KEY (id),

                                CONSTRAINT fk_question_votes_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id),

                                CONSTRAINT fk_question_votes_question
                                    FOREIGN KEY (question_id)
                                        REFERENCES questions(id),

                                CONSTRAINT uk_question_vote_user_question
                                    UNIQUE (user_id, question_id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE answer_votes (
                              id BINARY(16) NOT NULL,
                              created_at DATETIME(6) NOT NULL,
                              updated_at DATETIME(6) NOT NULL,
                              type VARCHAR(20) NOT NULL,

                              user_id BINARY(16) NOT NULL,
                              answer_id BINARY(16) NOT NULL,

                              PRIMARY KEY (id),

                              CONSTRAINT fk_answer_votes_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id),

                              CONSTRAINT fk_answer_votes_answer
                                  FOREIGN KEY (answer_id)
                                      REFERENCES answers(id),

                              CONSTRAINT uk_answer_vote_user_answer
                                  UNIQUE (user_id, answer_id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;