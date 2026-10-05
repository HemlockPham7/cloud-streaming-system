package com.streamingsystem.cloudservice.dto.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResponse {
    private Integer id;
    private String description;
    private String status;
    private String imageContentType;
    private String imageFileName;
    private Long imageSize;
}
