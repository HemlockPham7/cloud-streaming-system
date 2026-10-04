package com.streamingsystem.cloudservice.dto;

import java.io.InputStream;

public record FileObjectDTO(
        String name,
        String contentType,
        Long size,
        InputStream data
) {
}