package com.streamingsystem.cloudservice.controller;

import com.streamingsystem.cloudservice.dto.FileObjectDTO;
import com.streamingsystem.cloudservice.dto.ImageObjectDTO;
import com.streamingsystem.cloudservice.dto.VideoObjectDTO;
import com.streamingsystem.cloudservice.dto.process.ProcessCreateRequest;
import com.streamingsystem.cloudservice.dto.process.ProcessDetailResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessUpdateRequest;
import com.streamingsystem.cloudservice.service.ProcessService;
import com.streamingsystem.cloudservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/api/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;
    private final StorageService storageService;

    @Value("${spring.cloud.aws.bucket.sales.name}")
    private String bucket;

    @PostMapping(
            value = "/upload/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        FileObjectDTO fileObjectDTO = new FileObjectDTO(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        );

        String key = storageService.uploadFile(bucket, fileObjectDTO);

        return ResponseEntity.ok(key);
    }

    @PostMapping(
            value = "/upload/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        ImageObjectDTO imageObjectDTO = new ImageObjectDTO(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        );

        String key = storageService.uploadImage(bucket, imageObjectDTO);

        return ResponseEntity.ok(key);
    }

    @PostMapping(
            value = "/upload/video",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadVideo(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        VideoObjectDTO videoObjectDTO = new VideoObjectDTO(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream()
        );

        String key = storageService.uploadVideo(bucket, videoObjectDTO);

        return ResponseEntity.ok(key);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProcessResponse> createProcess(@ModelAttribute ProcessCreateRequest request) {
        try {
            ProcessResponse response = processService.createProcess(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{processId}")
    public ResponseEntity<ProcessDetailResponse> getProcessDetail(@PathVariable Integer processId) {
        ProcessDetailResponse response = processService.getProcessDetail(processId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{processId}")
    public ResponseEntity<ProcessResponse> updateProcess(
            @PathVariable Integer processId,
            @ModelAttribute ProcessUpdateRequest request
    ) {
        ProcessResponse response = processService.updateProcess(processId, request);
        return ResponseEntity.ok(response);
    }
}
