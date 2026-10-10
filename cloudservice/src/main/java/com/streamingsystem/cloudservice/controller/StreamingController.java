package com.streamingsystem.cloudservice.controller;

import com.streamingsystem.cloudservice.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/api/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;

    @GetMapping(value = "/{videoId}/playlists", produces = "application/x-mpegURL")
    public ResponseEntity<String> getSignedPlaylist(@PathVariable UUID videoId) {
      String signedMasterPlaylist = streamingService.getRewrittenMasterPlaylist(videoId);

      return ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_TYPE, "application/x-mpegURL")
              .body(signedMasterPlaylist);
    }

    @GetMapping(value = "/{videoId}/playlists/{resolution}", produces = "application/x-mpegURL")
    public ResponseEntity<String> getSubPlaylist(
            @PathVariable UUID videoId,
            @PathVariable String resolution) {

        String rewrittenSubPlaylist = streamingService.getRewrittenSubPlaylist(videoId, resolution);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/x-mpegURL")
                .body(rewrittenSubPlaylist);
    }
}
// Example for return of /{videoId}/playlists
//#EXTM3U
//#EXT-X-VERSION:3
//
//#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720,CODECS="avc1.42e01e,mp4a.40.2"
//http://streaming.localhost:4566/videos/6edc8098-be6d-4ffc-99af-6fe81402362d/hls/720p/index.m3u8?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261009T105725Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261009%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=4f7f4f324f74a8dc475b8170e71aa805ea501843d99fc2c1d643266b92e8b122
//
//#EXT-X-STREAM-INF:BANDWIDTH=1200000,RESOLUTION=854x480,CODECS="avc1.42e01e,mp4a.40.2"
//http://streaming.localhost:4566/videos/6edc8098-be6d-4ffc-99af-6fe81402362d/hls/480p/index.m3u8?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261009T105725Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261009%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=571308b2808f0aed84d11c7c1600130b960f5f6a09fd6f7506f62725356b9ba5//----------------
// Example for return of /{videoId}/playlists/{resolution}
//#EXTM3U
//#EXT-X-VERSION:3
//#EXT-X-TARGETDURATION:13
//#EXT-X-MEDIA-SEQUENCE:0
//#EXTINF:13.100000,
//http://streaming.localhost:4566/videos/6edc8098-be6d-4ffc-99af-6fe81402362d/hls/720p/segment_000.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261009T170848Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261009%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=e6934bb81a4a73193c7b9f86179ad88b503bb20be18b0038bff7761839eeec0d
//#EXTINF:8.333333,
//http://streaming.localhost:4566/videos/6edc8098-be6d-4ffc-99af-6fe81402362d/hls/720p/segment_001.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261009T170848Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261009%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=aeb91c1d7ae5f65e0503afed2182910e6c51a9bc58ad18716d04d957825bce57
//#EXTINF:1.633333,
//http://streaming.localhost:4566/videos/6edc8098-be6d-4ffc-99af-6fe81402362d/hls/720p/segment_002.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261009T170849Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261009%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=c89e2c949dfcf34b04e02b0cf6a1ca992cb0b149d050cd52db388abbcb786d00
//#EXT-X-ENDLIST