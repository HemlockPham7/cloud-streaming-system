package com.streamingsystem.cloudservice.controller;

import com.streamingsystem.cloudservice.dto.pagination.GenericPaginationResponse;
import com.streamingsystem.cloudservice.dto.video.VideoCreateRequest;
import com.streamingsystem.cloudservice.dto.video.VideoGetAllResponse;
import com.streamingsystem.cloudservice.dto.video.VideoResponse;
import com.streamingsystem.cloudservice.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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
            @RequestParam("category") String category,
            @RequestParam("author") String author,
            @RequestParam("thumbnail") MultipartFile thumbnail
    ) {
        if (!MediaType.IMAGE_PNG_VALUE.equals(thumbnail.getContentType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        VideoCreateRequest videoCreateRequest = new VideoCreateRequest(title, description, category, author);
        VideoResponse response = videoService.uploadVideo(file, thumbnail, videoCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<GenericPaginationResponse<VideoGetAllResponse>> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(value = "search", required = false) String search
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);

        GenericPaginationResponse<VideoGetAllResponse> response = videoService.getAllVideos(search, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(value = "/update/{videoId}")
    public ResponseEntity<String> uploadVideo(
            @PathVariable UUID videoId,
            @RequestParam("view_count") Long viewCount,
            @RequestParam("like_count") Long likeCount
    ) {
        videoService.updateVideoMetadata(videoId, viewCount, likeCount);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated!");
    }
}
