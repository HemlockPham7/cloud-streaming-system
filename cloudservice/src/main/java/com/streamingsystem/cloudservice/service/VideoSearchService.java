package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.event.dto.VideoReadyEvent;

public interface VideoSearchService {

    void indexVideo(VideoReadyEvent event);
}
