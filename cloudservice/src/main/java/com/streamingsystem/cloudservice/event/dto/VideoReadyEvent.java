package com.streamingsystem.cloudservice.event.dto;

import com.streamingsystem.cloudservice.dto.VideoStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VideoReadyEvent(
        UUID videoId,
        String title,
        String description,
        VideoStatus status,
        String author,
        String thumbnailKey,
        String thumbnailType,
        String category,
        Long viewCount,
        Long likeCount,
        LocalDateTime createdAt
) {
}
