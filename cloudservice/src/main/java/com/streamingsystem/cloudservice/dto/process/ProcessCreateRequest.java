package com.streamingsystem.cloudservice.dto.process;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProcessCreateRequest {
    private String description;
    private MultipartFile image;
}