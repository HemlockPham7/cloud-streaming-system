package com.streamingsystem.cloudservice.dto.streaming;

import lombok.Builder;

@Builder
public record StreamingResponse (
        String masterPlaylist,
        String basePath
) {
}
