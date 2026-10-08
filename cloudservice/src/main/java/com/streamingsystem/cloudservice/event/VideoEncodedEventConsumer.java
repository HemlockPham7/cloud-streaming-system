package com.streamingsystem.cloudservice.event;

import com.streamingsystem.cloudservice.event.dto.VideoEncodedEvent;
import com.streamingsystem.cloudservice.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoEncodedEventConsumer {

    private final VideoService videoService;

    @KafkaListener(topics = "video.encoded", containerFactory = "kafkaListenerContainerFactory")
    public void consumeVideoEncodedEvent(VideoEncodedEvent event) {
        log.info("Received VideoEncodedEvent for videoId: {}", event.videoId());
        try {
            videoService.handleVideoEncoded(event);
        } catch (Exception e) {
            log.error("Error processing VideoEncodedEvent for videoId: {}", event.videoId(), e);
            throw e;
        }
    }
}
