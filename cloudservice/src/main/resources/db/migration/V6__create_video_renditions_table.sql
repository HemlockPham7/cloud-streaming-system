DROP TABLE IF EXISTS video_renditions;

CREATE TABLE video_renditions
(
    id           UUID PRIMARY KEY,
    video_id     UUID NOT NULL,

    resolution   VARCHAR(20) NOT NULL,
    width        INTEGER NOT NULL,
    height       INTEGER NOT NULL,

    bitrate      BIGINT,
    frame_rate   DECIMAL(5,2),

    playlist_key VARCHAR(500) NOT NULL,

    created_at   TIMESTAMP NOT NULL,

    CONSTRAINT fk_video_rendition_video FOREIGN KEY (video_id) REFERENCES videos(id)
);
