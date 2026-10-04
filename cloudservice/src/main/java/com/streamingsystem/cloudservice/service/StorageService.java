package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.FileObjectDTO;
import com.streamingsystem.cloudservice.dto.ImageObjectDTO;
import com.streamingsystem.cloudservice.dto.VideoObjectDTO;

public interface StorageService {

    String uploadFile(FileObjectDTO fileObjectDTO);
    String uploadImage(ImageObjectDTO imageObjectDTO);
    String uploadVideo(VideoObjectDTO videoObjectDTO);

    void uploadImage();
    void uploadVideo();
}
