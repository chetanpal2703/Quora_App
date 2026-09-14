
-- ============================================================
-- Application-specific indexes
-- ============================================================

-- ------------------------------------------------------------
-- Question Tags
-- ------------------------------------------------------------

-- Supports queries that search question_tags by tag_id.
-- The primary key (question_id, tag_id) already supports
-- lookups starting with question_id.
--
-- tag_id needs its own index for the reverse direction:
-- WHERE tag_id = ?
--
-- This index already exists in the current database according
-- to SHOW INDEX, so this statement should NOT be executed again.
--
-- CREATE INDEX idx_question_tags_tag_id
-- ON question_tags(tag_id);


-- ------------------------------------------------------------
-- Question Full-Text Search
-- ------------------------------------------------------------

-- Specialized index for searching question title and content.
CREATE FULLTEXT INDEX idx_questions_fulltext
ON questions(title, content);

