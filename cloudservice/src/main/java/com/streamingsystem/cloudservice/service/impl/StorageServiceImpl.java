package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.dto.FileObjectDTO;
import com.streamingsystem.cloudservice.dto.ImageObjectDTO;
import com.streamingsystem.cloudservice.dto.VideoObjectDTO;
import com.streamingsystem.cloudservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private static final String BUCKET_FOLDER_FOR_FILES = "files";
    private static final String BUCKET_FOLDER_FOR_IMAGES = "images";
    private static final String BUCKET_FOLDER_FOR_VIDEOS = "videos";
    private final S3TransferManager s3TransferManager;

    @Override
    public String uploadImage(String bucket, ImageObjectDTO imageObjectDTO) {
        String key = BUCKET_FOLDER_FOR_IMAGES + "/" + imageObjectDTO.name();

        try (InputStream inputStream = imageObjectDTO.data()) {
            UploadRequest uploadRequest = UploadRequest.builder()
                    .putObjectRequest(builder -> builder
                            .bucket(bucket)
                            .key(key)
                            .contentType(imageObjectDTO.contentType())
                            .contentLength(imageObjectDTO.size())
                    )
                    .requestBody(
                            AsyncRequestBody.fromInputStream(inputStream, imageObjectDTO.size(), null)
                    )
                    .build();

            Upload upload = s3TransferManager.upload(uploadRequest);
            CompletedUpload completedUpload = upload.completionFuture().join();

            log.info(
                    "Image uploaded successfully. bucket={}, key={}, etag={}",
                    bucket,
                    key,
                    completedUpload.response().eTag()
            );
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public String uploadFile(String bucket, FileObjectDTO fileObjectDTO) {
        String key = BUCKET_FOLDER_FOR_FILES + "/" + fileObjectDTO.name();

        try (InputStream inputStream = fileObjectDTO.data()) {
            UploadRequest uploadRequest = UploadRequest.builder()
                    .putObjectRequest(builder -> builder
                            .bucket(bucket)
                            .key(key)
                            .contentType(fileObjectDTO.contentType())
                            .contentLength(fileObjectDTO.size())
                    )
                    .requestBody(
                            AsyncRequestBody.fromInputStream(inputStream, fileObjectDTO.size(), null)
                    )
                    .build();

            Upload upload = s3TransferManager.upload(uploadRequest);
            CompletedUpload completedUpload = upload.completionFuture().join();
            log.info(
                    "File uploaded successfully. bucket={}, key={}, etag={}",
                    bucket,
                    key,
                    completedUpload.response().eTag()
            );
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public String uploadVideo(String bucket, VideoObjectDTO videoObjectDTO) {
        String key = BUCKET_FOLDER_FOR_VIDEOS  + "/" + videoObjectDTO.name();

        try (InputStream inputStream = videoObjectDTO.data()) {
            UploadRequest uploadRequest = UploadRequest.builder()
                    .putObjectRequest(builder -> builder
                            .bucket(bucket)
                            .key(key)
                            .contentType(videoObjectDTO.contentType())
                            .contentLength(videoObjectDTO.size())
                    )
                    .requestBody(
                            AsyncRequestBody.fromInputStream(inputStream, videoObjectDTO.size(), null)
                    )
                    .build();

            Upload upload = s3TransferManager.upload(uploadRequest);
            CompletedUpload completedUpload = upload.completionFuture().join();
            log.info(
                    "Video uploaded successfully. bucket={}, key={}, etag={}",
                    bucket,
                    key,
                    completedUpload.response().eTag()
            );
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video", e);
        }
    }
}
