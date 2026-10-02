ALTER TABLE permissions
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Assuming you also have a roles table since you have permissions!
ALTER TABLE roles
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;