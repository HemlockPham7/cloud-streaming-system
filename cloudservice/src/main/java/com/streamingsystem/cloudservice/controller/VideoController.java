package com.streamingsystem.cloudservice.controller;

import com.streamingsystem.cloudservice.dto.video.VideoCreateRequest;
import com.streamingsystem.cloudservice.dto.video.VideoResponse;
import com.streamingsystem.cloudservice.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<VideoResponse> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category
    ) {
        VideoCreateRequest videoCreateRequest = new VideoCreateRequest(title, description, category);
        VideoResponse response = videoService.uploadVideo(file, videoCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // get all videos

    // get video by id

    // search video by elastic search
}
