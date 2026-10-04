package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.dto.process.ProcessCreateRequest;
import com.streamingsystem.cloudservice.dto.process.ProcessResponse;
import com.streamingsystem.cloudservice.entity.ProcessEntity;
import com.streamingsystem.cloudservice.repository.ProcessRepository;
import com.streamingsystem.cloudservice.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository processRepository;

    @Override
    public ProcessResponse createProcess(ProcessCreateRequest request) throws IOException {
        MultipartFile file = request.getImage();

        ProcessEntity entity = ProcessEntity.builder()
                .description(request.getDescription())
                .status("pending")
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
}
