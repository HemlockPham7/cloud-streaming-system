package com.streamingsystem.cloudservice.service;

import java.util.UUID;

public interface StreamingService {

    String getRewrittenMasterPlaylist(UUID videoId);
    String getRewrittenSubPlaylist(UUID videoId, String resolution);
}
