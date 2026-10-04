DROP TABLE IF EXISTS process_s3_migration;

CREATE TABLE process_s3_migration
(
    id          SERIAL PRIMARY KEY,
    process_id  INT NOT NULL UNIQUE,
    bucket_name VARCHAR(255) NOT NULL,
    object_key  VARCHAR(500) NOT NULL,
    migrated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_process_s3 FOREIGN KEY (process_id) REFERENCES process (id) ON DELETE CASCADE
);