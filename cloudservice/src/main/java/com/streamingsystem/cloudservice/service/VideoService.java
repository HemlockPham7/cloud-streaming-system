package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.video.VideoCreateRequest;
import com.streamingsystem.cloudservice.dto.video.VideoResponse;
import com.streamingsystem.cloudservice.event.dto.VideoEncodedEvent;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    VideoResponse uploadVideo(MultipartFile file, VideoCreateRequest request);
    void handleVideoEncoded(VideoEncodedEvent event);
}
