package com.streamingsystem.cloudservice.dto.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDetailResponse {
    private Integer id;
    private String description;
    private String status;
    private String imageUrl;
    private String imageContentType;
    private String imageFileName;
    private Long imageSize;
    private byte[] imageData;
}
