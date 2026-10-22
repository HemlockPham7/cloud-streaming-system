package com.streamingsystem.cloudservice.dto.streaming;

import lombok.Builder;

@Builder
public record StreamingResponse (
        String masterPlaylistPath,
        String masterPlaylistContent,
        String basePath
) {
}
