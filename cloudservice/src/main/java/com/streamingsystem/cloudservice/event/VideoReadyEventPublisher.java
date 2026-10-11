package com.streamingsystem.cloudservice.event;

import com.streamingsystem.cloudservice.event.dto.VideoReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoReadyEventPublisher {

    private static final String VIDEO_READY_TOPIC = "video.ready";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVideoReady(VideoReadyEvent event) {
        kafkaTemplate.send(VIDEO_READY_TOPIC, event.videoId().toString(), event)
                .whenComplete((_, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish VideoReadyEvent for videoId: {}", event.videoId(), ex);
                    } else {
                        log.info("Successfully published VideoReadyEvent for videoId: {}", event.videoId());
                    }
                });
    }
}
