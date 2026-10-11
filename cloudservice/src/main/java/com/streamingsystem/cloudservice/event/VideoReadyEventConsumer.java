package com.streamingsystem.cloudservice.event;

import com.streamingsystem.cloudservice.event.dto.VideoReadyEvent;
import com.streamingsystem.cloudservice.service.VideoSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoReadyEventConsumer {

    private final VideoSearchService videoSearchService;

    @KafkaListener(topics = "video.ready", containerFactory = "kafkaListenerContainerFactory")
    public void consumeVideoEncodedEvent(VideoReadyEvent event) {
        log.info("Received VideoReadyEvent for videoId: {}", event.videoId());
        try {
            videoSearchService.indexVideo(event);
        } catch (Exception e) {
            log.error("Error processing VideoReadyEvent for videoId: {}", event.videoId(), e);
            throw e;
        }
    }
}
