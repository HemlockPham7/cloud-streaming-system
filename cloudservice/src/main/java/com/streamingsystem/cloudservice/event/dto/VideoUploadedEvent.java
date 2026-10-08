package com.streamingsystem.cloudservice.event.dto;

import java.util.UUID;

public record VideoUploadedEvent(
        UUID videoId,
        String bucket,
        String originalKey,
        String originalFilename
) {}