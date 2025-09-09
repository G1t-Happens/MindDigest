-- V1__create_digest_entries_table.sql

-- ============================================================
-- Create the 'digest_entries' table with auditing timestamps
-- ============================================================

-- Note:
-- The "created" and "updated" timestamp fields are primarily managed by
-- JPA Auditing using the @CreatedDate and @LastModifiedDate annotations
-- in the corresponding Java entity.
-- This trigger serves as a fallback to ensure the "updated" timestamp
-- is correctly maintained if updates are made outside of JPA,
-- such as via raw SQL or external tools.
CREATE TABLE digest_entries
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(256),
    summary    VARCHAR(5000),
    author     VARCHAR(256),
    source_url VARCHAR(1000),
    created    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- ============================================================
-- Trigger function to update the 'updated' timestamp on change
-- ============================================================
CREATE OR REPLACE FUNCTION set_updated_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- Trigger to call the function before any update on the table
-- ============================================================
CREATE TRIGGER trg_set_updated_digest_entries
    BEFORE UPDATE
    ON digest_entries
    FOR EACH ROW
EXECUTE FUNCTION set_updated_timestamp();
