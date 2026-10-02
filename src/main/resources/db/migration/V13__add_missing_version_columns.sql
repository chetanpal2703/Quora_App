ALTER TABLE answer_votes
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- If you have a question_votes table that also extends BaseEntity, add it here too:
ALTER TABLE question_votes
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;