DROP TABLE IF EXISTS process;

CREATE TABLE process
(
    id          SERIAL PRIMARY KEY,
    description VARCHAR(100),
    image       BYTEA,
    status      VARCHAR(50) DEFAULT 'pending'
);