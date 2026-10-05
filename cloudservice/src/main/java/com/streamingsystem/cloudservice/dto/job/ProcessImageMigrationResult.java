package com.streamingsystem.cloudservice.dto.job;

public record ProcessImageMigrationResult(
        Integer processId,
        String bucketName,
        String objectKey
) {}