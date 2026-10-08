package com.streamingsystem.cloudservice.event;

import com.streamingsystem.cloudservice.event.dto.VideoUploadedEvent;
import com.streamingsystem.cloudservice.service.EncodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoUploadedEventConsumer {

    private final EncodingService encodingService;

    @KafkaListener(topics = "video.uploaded", containerFactory = "kafkaListenerContainerFactory")
    public void consumeVideoUploadedEvent(VideoUploadedEvent event) {
        log.info("Received VideoUploadedEvent for videoId: {}", event.videoId());
        try {
            encodingService.encodeVideo(event);
        } catch (Exception e) {
            log.error("Error processing VideoUploadedEvent for videoId: {}", event.videoId(), e);
        }
    }
}
