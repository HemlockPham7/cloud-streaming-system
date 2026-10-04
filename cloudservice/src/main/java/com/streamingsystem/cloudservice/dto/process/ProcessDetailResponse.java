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
    private byte[] image;
}
