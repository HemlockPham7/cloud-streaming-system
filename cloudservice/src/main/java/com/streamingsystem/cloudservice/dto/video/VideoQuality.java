package com.streamingsystem.cloudservice.dto.video;

public record VideoQuality(
        int width,
        int height,
        int bitrateKbps
) {

    public String getResolutionName() {
        return height + "p"; // Example: "720p", "480p"
    }
}