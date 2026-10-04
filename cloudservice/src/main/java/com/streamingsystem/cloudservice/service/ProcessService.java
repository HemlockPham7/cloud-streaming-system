package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.process.ProcessCreateRequest;
import com.streamingsystem.cloudservice.dto.process.ProcessDetailResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessResponse;
import com.streamingsystem.cloudservice.dto.process.ProcessUpdateRequest;

import java.io.IOException;

public interface ProcessService {

    ProcessResponse createProcess(ProcessCreateRequest request) throws IOException;
    ProcessDetailResponse getProcessDetail(Integer processId);
    ProcessResponse updateProcess(Integer processId, ProcessUpdateRequest request);
}
