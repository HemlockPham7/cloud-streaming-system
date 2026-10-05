package com.streamingsystem.cloudservice.processor.process;

import com.streamingsystem.cloudservice.dto.ImageObjectDTO;
import com.streamingsystem.cloudservice.dto.job.ProcessImageMigrationDTO;
import com.streamingsystem.cloudservice.dto.job.ProcessImageMigrationResult;
import com.streamingsystem.cloudservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessImageMigrationProcessor implements ItemProcessor<ProcessImageMigrationDTO, ProcessImageMigrationResult> {

    private final StorageService storageService;

    @Value("${spring.cloud.aws.bucket.process.name}")
    private String bucketName;

    @Override
    public ProcessImageMigrationResult process(ProcessImageMigrationDTO item) throws SQLException {
        log.info("Migrating process image. processId={}", item.processId());

        if (item.imageData() == null) {
            return null;
        }

        ImageObjectDTO imageObjectDTO = new ImageObjectDTO(
                item.imageFileName(),
                item.imageContentType(),
                item.imageSize(),
                new ByteArrayInputStream(item.imageData())
        );
        String objectKey = storageService.uploadImage(bucketName, imageObjectDTO);

        return new ProcessImageMigrationResult(
                item.processId(),
                bucketName,
                objectKey
        );
    }
}