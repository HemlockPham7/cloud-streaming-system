package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.streaming.StreamingResponse;

import java.util.UUID;

public interface StreamingService {

    StreamingResponse getMasterPlaylist(UUID videoId);
    String getRewrittenMasterPlaylist(UUID videoId);
    String getRewrittenSubPlaylist(UUID videoId, String resolution);
}
