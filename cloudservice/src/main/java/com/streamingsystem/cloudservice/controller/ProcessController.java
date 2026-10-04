package com.streamingsystem.cloudservice.controller;

import com.streamingsystem.cloudservice.dto.process.ProcessCreateRequest;
import com.streamingsystem.cloudservice.dto.process.ProcessDetailResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessUpdateRequest;
import com.streamingsystem.cloudservice.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/api/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

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
