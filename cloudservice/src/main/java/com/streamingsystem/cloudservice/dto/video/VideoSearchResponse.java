package com.streamingsystem.cloudservice.dto.video;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VideoSearchResponse(
        UUID id,
        String title,
        String description,
        String author,
        String thumbnailKey,
        String thumbnailType,
        String category,
        Long viewCount,
        Long likeCount,
        LocalDateTime createdAt
) {
}
