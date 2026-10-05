DROP TABLE IF EXISTS videos;

CREATE TABLE videos
(
    id                 UUID PRIMARY KEY,

    title              VARCHAR(255) NOT NULL,
    description        VARCHAR(500),
    status             VARCHAR(30) NOT NULL,
    category           VARCHAR(50),

    original_key       VARCHAR(500) NOT NULL,
    hls_master_key     VARCHAR(500),

    original_filename  VARCHAR(500),
    content_type       VARCHAR(100),

    file_size          BIGINT,
    duration_seconds   BIGINT,
    width              INTEGER,
    height             INTEGER,

    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP NOT NULL
);
