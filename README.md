# cloud-streaming-system
Feature 1 - Set scheduler to upload image to s3 storage and change the path to image. Feature 2 - Uploading video and streaming video by hls. 

{
"data": [
{
"id": "eb407271-1153-47fe-a87b-298143a09a2d",
"title": "Test",
"description": "Video testing endpoint",
"status": "READY",
"author": "TEST",
"thumbnailKey": "https://streaming.s3.ap-southeast-1.amazonaws.com/videos/eb407271-1153-47fe-a87b-298143a09a2d/thumbnail.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261009T073459Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261009%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=900&X-Amz-Signature=c053f2c4894df1c810de2413e9c83d6b96bd6e2a7cec36a8201054b5c55725b9",
"thumbnailType": "image/png",
"category": "TEST",
"viewCount": 7856,
"likeCount": 98,
"createdAt": "2026-10-09T14:33:59.524469"
},
{
"id": "a0a697e9-5cd5-49fd-9904-1011f0d94938",
"title": "KHTN",
"description": "University of Science",
"status": "READY",
"author": "Viet Hoang",
"thumbnailKey": "https://streaming.s3.ap-southeast-1.amazonaws.com/videos/a0a697e9-5cd5-49fd-9904-1011f0d94938/thumbnail.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261009T073459Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261009%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=900&X-Amz-Signature=d688fbff9b6ee0783005ffa9c3e59a7831771dd6c37ba1a6346b23a15072bbbb",
"thumbnailType": "image/png",
"category": "APCS",
"viewCount": 2312254,
"likeCount": 23124,
"createdAt": "2026-10-09T14:11:08.016992"
}
],
"pagination": {
"page": 0,
"size": 10,
"totalElements": 2,
"totalPages": 1
}
}
---
package com.streamingsystem.cloudservice.dto.pagination;

import java.io.Serializable;
import java.util.List;

public record GenericPaginationResponse<T> (
List<T> data,
PaginationResponse pagination
) implements Serializable {
}
---
package com.streamingsystem.cloudservice.dto.pagination;

import java.io.Serializable;

public record PaginationResponse (
int page,
int size,
long totalElements,
int totalPages
) implements Serializable {
}
---
package com.streamingsystem.cloudservice.dto.video;

import com.streamingsystem.cloudservice.dto.VideoStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VideoGetAllResponse(
UUID id,
String title,
String description,
VideoStatus status,
String author,
String thumbnailKey,
String thumbnailType,
String category,
Long viewCount,
Long likeCount,
LocalDateTime createdAt
) {
}
