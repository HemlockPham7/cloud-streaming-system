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

    @GetMapping(value = "/{videoId}/playlist", produces = "application/x-mpegURL")
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

//#EXTM3U
//#EXT-X-VERSION:3
//
//#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720,CODECS="avc1.42e01e,mp4a.40.2"
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/720p/index.m3u8?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151521Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=f7ec9b61f8bec52062b343d22a853106a9178dac33988e44c7a84b6007111cc6
//
//#EXT-X-STREAM-INF:BANDWIDTH=1200000,RESOLUTION=854x480,CODECS="avc1.42e01e,mp4a.40.2"
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/480p/index.m3u8?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151521Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=aff763b50cab574d6c21d6be49f708f1a1754e425f7bdcb067ecd3aef7543764
//#EXTM3U
//#EXT-X-VERSION:3
//#EXT-X-TARGETDURATION:14
//#EXT-X-MEDIA-SEQUENCE:0
//#EXTINF:13.766667,
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/480p/segment_000.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151602Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=ae7245cf32a1dbf465b665372ca45eeb42b643bfa00ed681b64ea02f156f3ad1
//#EXTINF:8.333333,
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/480p/segment_001.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151602Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=b75e173b3b49d165972b89cda26badf0145a6a7c70a7bb0fae11824276c0142c
//#EXTINF:0.966667,
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/480p/segment_002.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151602Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=d7d50b25f4de2fd108f095839a7424341eec8ca51b8b5fb8b659f1430a6e09f0
//#EXT-X-ENDLIST
//---------
//#EXTM3U
//#EXT-X-VERSION:3
//#EXT-X-TARGETDURATION:13
//#EXT-X-MEDIA-SEQUENCE:0
//#EXTINF:13.100000,
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/720p/segment_000.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151723Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=4eacd0e513166bd7cb47f9c50519d444c383a89e2610c7992b6677876d0702bf
//#EXTINF:8.333333,
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/720p/segment_001.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151723Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=9e8e1a5fbecf96c40d0613933ce32cf6bd57201b5de39a0d1ddd26ce6fff82fd
//#EXTINF:1.633333,
//https://streaming.s3.ap-southeast-1.amazonaws.com/videos/bf6f0981-15f3-48d7-8fcd-0026f761bb1d/hls/720p/segment_002.ts?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20261008T151723Z&X-Amz-SignedHeaders=host&X-Amz-Credential=test%2F20261008%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Expires=86400&X-Amz-Signature=b49997e05e78b27c4fff2dd9ef24d0c3f6f8bcdbb9c3ad99bc113fe576b93d84
//#EXT-X-ENDLIST