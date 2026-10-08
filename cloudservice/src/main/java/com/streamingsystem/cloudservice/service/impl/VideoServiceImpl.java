package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.dto.VideoStatus;
import com.streamingsystem.cloudservice.dto.video.VideoCreateRequest;
import com.streamingsystem.cloudservice.dto.video.VideoResponse;
import com.streamingsystem.cloudservice.entity.VideoEntity;
import com.streamingsystem.cloudservice.entity.VideoRenditionEntity;
import com.streamingsystem.cloudservice.event.dto.VideoEncodedEvent;
import com.streamingsystem.cloudservice.event.dto.VideoUploadedEvent;
import com.streamingsystem.cloudservice.repository.VideoRenditionRepository;
import com.streamingsystem.cloudservice.repository.VideoRepository;
import com.streamingsystem.cloudservice.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private static final String VIDEO_UPLOADED_TOPIC = "video.uploaded";

    @Value("${spring.cloud.aws.bucket.streaming.name}")
    private String bucket;

    private final VideoRepository videoRepository;
    private final VideoRenditionRepository videoRenditionRepository;

    private final S3TransferManager s3TransferManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public VideoResponse uploadVideo(MultipartFile file, VideoCreateRequest request) {
        UUID videoId = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename();

        // Step 1: Upload raw video to s3 with key videos/{videoId}/original.mp4
        String key = String.format("videos/%s/original.mp4", videoId);
        uploadToS3(file, key);

        // Step 2: Store Video Entity with initial status is PROCESSING
        LocalDateTime now = LocalDateTime.now();
        VideoEntity videoEntity = VideoEntity.builder()
                .id(videoId)
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .status(VideoStatus.PROCESSING)
                .originalKey(key)
                .originalFilename(originalFilename)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .createdAt(now)
                .updatedAt(now)
                .build();

        VideoEntity savedEntity = videoRepository.save(videoEntity);

        VideoUploadedEvent event = new VideoUploadedEvent(
                savedEntity.getId(),
                bucket,
                savedEntity.getOriginalKey(),
                savedEntity.getOriginalFilename()
        );

        kafkaTemplate.send(VIDEO_UPLOADED_TOPIC, savedEntity.getId().toString(), event)
                .whenComplete((_, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish VideoUploadedEvent for videoId: {}", savedEntity.getId(), ex);
                    } else {
                        log.info("Successfully published VideoUploadedEvent for videoId: {}", savedEntity.getId());
                    }
                });

        return mapToResponse(savedEntity);
    }

    private void uploadToS3(MultipartFile file, String key) {
        try (InputStream inputStream = file.getInputStream()) {
            UploadRequest uploadRequest = UploadRequest.builder()
                    .putObjectRequest(builder -> builder
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                    )
                    .requestBody(AsyncRequestBody.fromInputStream(inputStream, file.getSize(), null))
                    .build();

            Upload upload = s3TransferManager.upload(uploadRequest);
            CompletedUpload completedUpload = upload.completionFuture().join();

            log.info("Raw video uploaded successfully to S3. bucket={}, key={}, etag={}",
                    bucket, key, completedUpload.response().eTag());
        } catch (IOException e) {
            log.error("Error reading file input stream for key={}", key, e);
            throw new RuntimeException("Failed to upload video to S3", e);
        }
    }

    private VideoResponse mapToResponse(VideoEntity entity) {
        return new VideoResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCategory(),
                entity.getOriginalKey(),
                entity.getOriginalFilename(),
                entity.getContentType(),
                entity.getFileSize(),
                entity.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void handleVideoEncoded(VideoEncodedEvent event) {
        log.info("Handling VideoEncodedEvent for videoId: {}", event.videoId());

        VideoEntity video = videoRepository.findById(event.videoId())
                .orElseThrow(() -> new IllegalArgumentException("Video not found with id: " + event.videoId()));

        // Check Idempotency
        if (VideoStatus.READY.equals(video.getStatus())) {
            log.warn("Video with id {} is already in READY status. Skipping processing.", event.videoId());
            return;
        }

        // update Video entity
        LocalDateTime now = LocalDateTime.now();
        video.setStatus(VideoStatus.READY);
        video.setHlsMasterKey(event.hlsMasterKey());
        video.setUpdatedAt(now);
        videoRepository.save(video);

        // create VideoRenditionEntity
        List<VideoRenditionEntity> renditions = event.renditions().stream()
                .map(renditionInfo -> VideoRenditionEntity.builder()
                        .video(video)
                        .resolution(renditionInfo.resolution())
                        .width(renditionInfo.width())
                        .height(renditionInfo.height())
                        .bitrate(renditionInfo.bitrate())
                        .playlistKey(renditionInfo.playlistKey())
                        .createdAt(now)
                        .build())
                .toList();

        videoRenditionRepository.saveAll(renditions);
        log.info("Successfully updated video status to READY and saved {} renditions for videoId: {}", renditions.size(), event.videoId());
    }
}
