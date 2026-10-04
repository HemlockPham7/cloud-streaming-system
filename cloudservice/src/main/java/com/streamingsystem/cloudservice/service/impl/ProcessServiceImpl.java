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
import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
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
            InputStream inputStream = file.getInputStream();
            entity.setImage(BlobProxy.generateProxy(inputStream, file.getSize()));
        }

        ProcessEntity saved = processRepository.save(entity);

        return ProcessResponse.builder()
                .id(saved.getId())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public ProcessDetailResponse getProcessDetail(Integer processId) {
        ProcessEntity process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found: " + processId));


        byte[] imageBytes = null;
        String imageUrl = null;
        if (process.getImage() != null) {
            try {
                Blob blob = process.getImage();
                imageBytes = blob.getBytes(1, (int) blob.length());
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Failed to read image from database", e
                );
            }
        } else {
            String status = process.getStatus();
            if ("COMPLETED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                ProcessS3MigrationEntity migration = process.getS3Migration();

                if (migration != null) {
                    imageUrl = generatePresignedUrl(migration);
                }
            }
        }

        return ProcessDetailResponse.builder()
                .id(process.getId())
                .description(process.getDescription())
                .status(process.getStatus())
                .imageUrl(imageUrl)
                .image(imageBytes)
                .build();
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
                .build();
    }
}
