DROP TABLE IF EXISTS process;

CREATE TABLE process
(
    id                 SERIAL PRIMARY KEY,
    description        VARCHAR(100),

    image_file_name     VARCHAR(255),
    image_content_type  VARCHAR(100),
    image_size          BIGINT,
    image_data          BYTEA,

    status             VARCHAR(50) DEFAULT 'PENDING'
);