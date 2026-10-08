package com.streamingsystem.cloudservice.event.dto;

import java.util.List;
import java.util.UUID;

public record VideoEncodedEvent(
        UUID videoId,
        String hlsMasterKey,
        List<RenditionInfo> renditions
) {
    public record RenditionInfo(
            String resolution,
            int width,
            int height,
            long bitrate,
            String playlistKey
    ) {}
}