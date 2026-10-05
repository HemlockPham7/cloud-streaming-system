package com.streamingsystem.cloudservice.dto.job;

public record ProcessImageMigrationDTO(
        Integer processId,

        String imageFileName,
        String imageContentType,
        Long imageSize,
        byte[] imageData,

        String status
) {}