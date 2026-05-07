CREATE TABLE IF NOT EXISTS undo_log (
    id BIGSERIAL PRIMARY KEY,
    branch_id BIGINT NOT NULL,
    xid VARCHAR(100) NOT NULL,
    context VARCHAR(128) NOT NULL,
    rollback_info BYTEA NOT NULL,
    log_status INT NOT NULL,
    log_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    log_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)
);

CREATE INDEX idx_undo_log_created ON undo_log (log_created);
