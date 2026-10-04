package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.process.ProcessCreateRequest;
import com.streamingsystem.cloudservice.dto.process.ProcessResponse;

import java.io.IOException;

public interface ProcessService {

    ProcessResponse createProcess(ProcessCreateRequest request) throws IOException;
}
