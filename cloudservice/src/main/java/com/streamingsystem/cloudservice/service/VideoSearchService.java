package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.pagination.GenericPaginationResponse;
import com.streamingsystem.cloudservice.dto.video.VideoSearchRequest;
import com.streamingsystem.cloudservice.dto.video.VideoSearchResponse;
import com.streamingsystem.cloudservice.event.dto.VideoReadyEvent;

import java.util.List;

public interface VideoSearchService {

    void indexVideo(VideoReadyEvent event);
    List<String> fetchSuggestions(String prefix);
    GenericPaginationResponse<VideoSearchResponse> searchVideos(VideoSearchRequest request);
}
