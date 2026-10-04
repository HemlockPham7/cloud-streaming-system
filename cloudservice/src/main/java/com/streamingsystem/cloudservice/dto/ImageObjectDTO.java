package com.streamingsystem.cloudservice.dto;

import java.io.InputStream;

public record ImageObjectDTO(
        String name,
        String contentType,
        Long size,
        InputStream data
) {
}