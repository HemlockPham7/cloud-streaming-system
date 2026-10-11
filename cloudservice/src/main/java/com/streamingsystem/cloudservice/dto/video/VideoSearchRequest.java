package com.streamingsystem.cloudservice.dto.video;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VideoSearchRequest(
        String query,
        @NotNull(message = "page field is required")
        Integer page,
        @NotNull(message = "size field is required")
        Integer size
) {
}
