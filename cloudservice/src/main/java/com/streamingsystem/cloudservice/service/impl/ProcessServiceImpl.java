package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.dto.process.ProcessCreateRequest;
import com.streamingsystem.cloudservice.dto.process.ProcessDetailResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessUpdateRequest;
import com.streamingsystem.cloudservice.entity.ProcessEntity;
import com.streamingsystem.cloudservice.entity.ProcessS3MigrationEntity;
import com.streamingsystem.cloudservice.repository.ProcessRepository;
import com.streamingsystem.cloudservice.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository processRepository;
    private final S3Presigner s3Presigner;

    @Override
    public ProcessResponse createProcess(ProcessCreateRequest request) throws IOException {
        MultipartFile file = request.getImage();

        ProcessEntity entity = ProcessEntity.builder()
                .description(request.getDescription())
                .status("PENDING")
                .build();

        if (file != null && !file.isEmpty()) {
            entity.setImageData(file.getBytes());
            entity.setImageContentType(file.getContentType());
            entity.setImageSize(file.getSize());
            entity.setImageFileName(file.getOriginalFilename());
        }

        ProcessEntity saved = processRepository.save(entity);

        return ProcessResponse.builder()
                .id(saved.getId())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .imageContentType(saved.getImageContentType())
                .imageFileName(saved.getImageFileName())
                .imageSize(saved.getImageSize())
                .build();
    }

    @Override
    public ProcessDetailResponse getProcessDetail(Integer processId) {
        ProcessEntity process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found: " + processId));

        ProcessDetailResponse processDetailResponse = ProcessDetailResponse.builder()
                .id(process.getId())
                .description(process.getDescription())
                .status(process.getStatus())
                .imageContentType(process.getImageContentType())
                .imageFileName(process.getImageFileName())
                .imageSize(process.getImageSize())
                .build();

        if (process.getImageData() != null) {
            processDetailResponse.setImageData(process.getImageData());
        } else {
            String status = process.getStatus();
            if ("COMPLETED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                ProcessS3MigrationEntity migration = process.getS3Migration();

                if (migration != null) {
                    processDetailResponse.setImageUrl(generatePresignedUrl(migration));
                }
            }
        }

        return processDetailResponse;
    }

    private String generatePresignedUrl(ProcessS3MigrationEntity migration) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(migration.getBucketName())
                .key(migration.getObjectKey())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    @Override
    public ProcessResponse updateProcess(Integer processId, ProcessUpdateRequest request) {
        ProcessEntity process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found: " + processId));

        if (request.getDescription() != null) {
            process.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            process.setStatus(request.getStatus());
        }

        ProcessEntity saved = processRepository.save(process);

        return ProcessResponse.builder()
                .id(saved.getId())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .imageContentType(saved.getImageContentType())
                .imageFileName(saved.getImageFileName())
                .imageSize(saved.getImageSize())
                .build();
    }
}
