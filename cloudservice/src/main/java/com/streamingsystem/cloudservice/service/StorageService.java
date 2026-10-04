package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.FileObjectDTO;

public interface StorageService {

    String uploadFile(FileObjectDTO fileObjectDTO);

    void uploadImage();
    void uploadVideo();
}
