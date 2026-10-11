package com.streamingsystem.cloudservice.dto.elasticsearch;

import java.time.LocalDateTime;

public record SearchRequest(
        String query,
        String category,
        Long viewCount,
        Long likeCount,
        LocalDateTime createdAt
) {
}
