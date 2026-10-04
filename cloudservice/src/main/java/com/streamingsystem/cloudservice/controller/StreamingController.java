package com.streamingsystem.cloudservice.controller;

import com.streamingsystem.cloudservice.dto.FileObjectDTO;
import com.streamingsystem.cloudservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/api/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StorageService storageService;

    @PostMapping(
            value = "/upload",
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

        String key = storageService.uploadFile(fileObjectDTO);

        return ResponseEntity.ok(key);
    }
}
