package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.pagination.GenericPaginationResponse;
import com.streamingsystem.cloudservice.dto.video.VideoCreateRequest;
import com.streamingsystem.cloudservice.dto.video.VideoGetAllResponse;
import com.streamingsystem.cloudservice.dto.video.VideoResponse;
import com.streamingsystem.cloudservice.event.dto.VideoEncodedEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public interface VideoService {

    VideoResponse uploadVideo(MultipartFile file, MultipartFile thumbnail, VideoCreateRequest request);
    void handleVideoEncoded(VideoEncodedEvent event);

    GenericPaginationResponse<VideoGetAllResponse> getAllVideos(String search, Pageable pageable);
    void updateVideoMetadata(UUID videoId, Long viewCount, Long likeCount);
}
