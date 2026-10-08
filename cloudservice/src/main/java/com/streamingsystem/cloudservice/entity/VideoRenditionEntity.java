package com.streamingsystem.cloudservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "video_renditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRenditionEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private VideoEntity video;

    @Column(nullable = false, length = 20)
    private String resolution;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    private Long bitrate;

    @Column(name = "frame_rate", precision = 5, scale = 2)
    private BigDecimal frameRate;

    @Column(name = "playlist_key", nullable = false, length = 500)
    private String playlistKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
