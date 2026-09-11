CREATE TABLE tags (
                      id BINARY(16) NOT NULL,
                      created_at DATETIME(6) NOT NULL,
                      updated_at DATETIME(6) NOT NULL,
                      name VARCHAR(50) NOT NULL,

                      PRIMARY KEY (id),
                      CONSTRAINT uk_tags_name UNIQUE (name)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE question_tags (
                               question_id BINARY(16) NOT NULL,
                               tag_id BINARY(16) NOT NULL,

                               PRIMARY KEY (question_id, tag_id),

                               CONSTRAINT fk_question_tags_question
                                   FOREIGN KEY (question_id)
                                       REFERENCES questions(id),

                               CONSTRAINT fk_question_tags_tag
                                   FOREIGN KEY (tag_id)
                                       REFERENCES tags(id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;