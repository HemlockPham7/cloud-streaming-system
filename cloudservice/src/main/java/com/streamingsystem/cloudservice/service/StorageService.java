package com.streamingsystem.cloudservice.service;

import com.streamingsystem.cloudservice.dto.FileObjectDTO;
import com.streamingsystem.cloudservice.dto.ImageObjectDTO;
import com.streamingsystem.cloudservice.dto.VideoObjectDTO;

public interface StorageService {

    String uploadFile(String bucket, FileObjectDTO fileObjectDTO);
    String uploadImage(String bucket, ImageObjectDTO imageObjectDTO);
    String uploadVideo(String bucket, VideoObjectDTO videoObjectDTO);
}
