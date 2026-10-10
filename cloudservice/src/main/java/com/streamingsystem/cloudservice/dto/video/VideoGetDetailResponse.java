package com.streamingsystem.cloudservice.dto.video;

import com.streamingsystem.cloudservice.dto.VideoStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VideoGetDetailResponse(
        UUID id,
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
