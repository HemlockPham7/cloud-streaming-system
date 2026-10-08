package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.event.dto.VideoUploadedEvent;

public interface EncodingService {

    void encodeVideo(VideoUploadedEvent event);
}
