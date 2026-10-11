package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.pagination.GenericPaginationResponse;
import com.streamingsystem.cloudservice.dto.video.VideoSearchRequest;
import com.streamingsystem.cloudservice.event.dto.VideoReadyEvent;
import com.streamingsystem.cloudservice.model.VideoSearchDocument;

import java.util.List;

public interface VideoSearchService {

    void indexVideo(VideoReadyEvent event);
    List<String> fetchSuggestions(String prefix);
    GenericPaginationResponse<VideoSearchDocument> searchVideos(VideoSearchRequest request);
}
