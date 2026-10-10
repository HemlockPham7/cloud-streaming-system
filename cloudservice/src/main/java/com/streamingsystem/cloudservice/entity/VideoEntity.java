package com.streamingsystem.cloudservice.entity;

import com.streamingsystem.cloudservice.dto.VideoStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VideoStatus status;

    @Column(length = 50)
    private String category;

    @Column(length = 255)
    private String author;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "thumbnail_key", length = 500)
    private String thumbnailKey;

    @Column(name = "thumbnail_type", length = 100)
    private String thumbnailType;

    @Column(name = "original_key", nullable = false, length = 500)
    private String originalKey;

    @Column(name = "hls_master_key", length = 500)
    private String hlsMasterKey;

    @Column(name = "original_filename", length = 500)
    private String originalFilename;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    private Integer width;

    private Integer height;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "video",
            fetch = FetchType.LAZY
    )
    private List<VideoRenditionEntity> renditions;
}
