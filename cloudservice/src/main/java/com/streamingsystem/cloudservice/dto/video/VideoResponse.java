package com.streamingsystem.cloudservice.dto.video;

import com.streamingsystem.cloudservice.dto.VideoStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VideoResponse(
        UUID id,
        String title,
        String description,
        VideoStatus status,
        String category,
        String originalKey,
        String originalFilename,
        String contentType,
        Long fileSize,
        LocalDateTime createdAt
) {
}
