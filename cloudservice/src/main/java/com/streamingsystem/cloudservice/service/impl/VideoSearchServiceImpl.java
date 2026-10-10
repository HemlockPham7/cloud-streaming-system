package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.event.dto.VideoReadyEvent;
import com.streamingsystem.cloudservice.model.VideoSearchDocument;
import com.streamingsystem.cloudservice.repository.VideoSearchRepository;
import com.streamingsystem.cloudservice.service.VideoSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VideoSearchServiceImpl implements VideoSearchService {

    private final VideoSearchRepository videoSearchRepository;

    @Transactional(readOnly = true)
    @Override
    public void indexVideo(VideoReadyEvent event) {
        VideoSearchDocument document = VideoSearchDocument.builder()
                .id(event.videoId())
                .title(event.title())
                .description(event.description())
                .author(event.author())
                .category(event.category())
                .thumbnailKey(event.thumbnailKey())
                .thumbnailType(event.thumbnailType())
                .viewCount(ThreadLocalRandom.current().nextLong(1, 10_000_001))
                .likeCount(ThreadLocalRandom.current().nextLong(1, 10_000_001))
                .createdAt(event.createdAt())
                .build();

        videoSearchRepository.save(document);
    }
}
