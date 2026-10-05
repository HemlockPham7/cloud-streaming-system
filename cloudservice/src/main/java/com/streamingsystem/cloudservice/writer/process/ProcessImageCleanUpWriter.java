package com.streamingsystem.cloudservice.writer.process;

import com.streamingsystem.cloudservice.dto.job.ProcessImageMigrationCleanUpDTO;
import com.streamingsystem.cloudservice.repository.ProcessCriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcessImageCleanUpWriter implements ItemWriter<ProcessImageMigrationCleanUpDTO> {

    private final ProcessCriteriaBuilder processCriteriaBuilder;

    @Override
    public void write(Chunk<? extends ProcessImageMigrationCleanUpDTO> items) {

        List<Integer> processIds = items.getItems()
                .stream()
                .map(ProcessImageMigrationCleanUpDTO::processId)
                .toList();

        processCriteriaBuilder.clearImages(processIds);
    }
}
