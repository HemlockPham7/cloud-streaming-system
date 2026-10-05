package com.streamingsystem.cloudservice.writer.process;

import com.streamingsystem.cloudservice.dto.job.ProcessImageMigrationResult;
import com.streamingsystem.cloudservice.entity.ProcessS3MigrationEntity;
import com.streamingsystem.cloudservice.repository.ProcessS3MigrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessImageMigrationWriter implements ItemWriter<ProcessImageMigrationResult> {

    private final ProcessS3MigrationRepository repository;

    @Override
    public void write(Chunk<? extends ProcessImageMigrationResult> items) {

        List<ProcessS3MigrationEntity> entities = items.getItems()
                .stream()
                .map(item -> ProcessS3MigrationEntity.builder()
                        .processId(item.processId())
                        .bucketName(item.bucketName())
                        .objectKey(item.objectKey())
                        .build())
                .toList();

        repository.saveAll(entities);
    }
}