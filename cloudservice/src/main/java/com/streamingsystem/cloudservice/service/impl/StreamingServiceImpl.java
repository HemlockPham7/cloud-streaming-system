package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.dto.VideoStatus;
import com.streamingsystem.cloudservice.entity.VideoEntity;
import com.streamingsystem.cloudservice.repository.VideoRepository;
import com.streamingsystem.cloudservice.service.StreamingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamingServiceImpl implements StreamingService {

    private final S3TransferManager s3TransferManager;
    private final S3Presigner s3Presigner;
    private final VideoRepository videoRepository;
    private final RedissonClient redissonClient;

    @Value("${spring.cloud.aws.bucket.streaming.name}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.presigned-url-expiry:86400}")
    private long presignedUrlExpirySeconds;

    private static final String MASTER_KEY_CACHE_PREFIX = "streaming:master_key:";
    private static final String PLAYLIST_CONTENT_CACHE_PREFIX = "streaming:playlist_cache:";
    private static final String SUB_M3U8_CACHE_PREFIX = "streaming:sub_m3u8:";

    /**
     * Get streaming URL for a video
     * FLOW:
     * 1. Check redis cache for existing presigned url
     * 2. if cached - return immediately.
     * 3. If not cached = rewritten the master playlist with presigned url from s3
     * 4. cache the url in redis
     * 5. return streaming url
     */
    @Override
    public String getRewrittenMasterPlaylist(UUID videoId) {
        // Step 1
        String cacheKey = PLAYLIST_CONTENT_CACHE_PREFIX + videoId;
        RBucket<String> playlistCache = redissonClient.getBucket(cacheKey);
        // Step 2
        String cachedContent = playlistCache.get();
        if (cachedContent != null) {
            log.debug("Returning cached rewritten master.m3u8 for videoId: {}", videoId);
            return cachedContent;
        }

        // Step 3
        log.info("Getting rewritten master.m3u8 for videoId: {}", videoId);
        String masterKey = getOrCacheMasterKey(videoId);
        String rawMasterContent = readFromS3UsingTransferManager(masterKey);
        // Example: "videos/{videoId}/hls/"
        String basePath = masterKey.contains("/")
                ? masterKey.substring(0, masterKey.lastIndexOf("/") + 1)
                : "";
        String rewrittenMasterContent = rewriteMasterContent(rawMasterContent, basePath);

        // Step 4: Cache the rewritten master.m3u8 content in Redis (with a TTL shorter than the presigned URL's expiration time to avoid using expired URLs).
        long cacheTtlSeconds = Math.max(5, presignedUrlExpirySeconds - 60 * 60);
        playlistCache.set(rewrittenMasterContent, Duration.of(cacheTtlSeconds, TimeUnit.SECONDS.toChronoUnit()));

        // Step 5
        return rewrittenMasterContent;
    }

    @Override
    public String getRewrittenSubPlaylist(UUID videoId, String resolution) {
        // Step 1
        String cacheKey = SUB_M3U8_CACHE_PREFIX + videoId + ":" + resolution;
        RBucket<String> subPlaylistCache = redissonClient.getBucket(cacheKey);
        // Step 2
        String cachedContent = subPlaylistCache.get();
        if (cachedContent != null) {
            log.debug("Returning cached rewritten sub-playlist for videoId: {}, res: {}", videoId, resolution);
            return cachedContent;
        }
        // Step 3
        String masterKey = getOrCacheMasterKey(videoId);
        String basePath = masterKey.contains("/")
                ? masterKey.substring(0, masterKey.lastIndexOf("/") + 1)
                : "";
        // "videos/{videoId}/hls/720p/index.m3u8"
        String subPlaylistKey = basePath + resolution + "/index.m3u8";
        String rawSubContent = readFromS3UsingTransferManager(subPlaylistKey);
        // Path folder contains file .ts: "videos/{videoId}/hls/720p/"
        String segmentFolderBasePath = basePath + resolution + "/";
        String rewrittenSubContent = rewriteSubPlaylistContent(rawSubContent, segmentFolderBasePath);
        // Step 4
        long cacheTtlSeconds = Math.max(5, presignedUrlExpirySeconds - 60 * 60);
        subPlaylistCache.set(rewrittenSubContent, Duration.of(cacheTtlSeconds, TimeUnit.SECONDS.toChronoUnit()));
        // Step 5
        return rewrittenSubContent;
    }

    private String getOrCacheMasterKey(UUID videoId) {
        String cacheKey = MASTER_KEY_CACHE_PREFIX + videoId;
        RBucket<String> masterKeyBucket = redissonClient.getBucket(cacheKey);

        String masterKey = masterKeyBucket.get();
        if (masterKey != null) {
            return masterKey;
        }

        VideoEntity video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + videoId));

        if (!VideoStatus.READY.equals(video.getStatus())) {
            throw new IllegalStateException("Video is not ready for streaming. Current status: " + video.getStatus());
        }

        masterKey = video.getHlsMasterKey();
        masterKeyBucket.set(masterKey, Duration.of(presignedUrlExpirySeconds, TimeUnit.SECONDS.toChronoUnit()));
        return masterKey;
    }

    private String readFromS3UsingTransferManager(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            // DownloadRequest with AsyncResponseTransformer.toBytes()
            DownloadRequest<ResponseBytes<GetObjectResponse>> downloadRequest = DownloadRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .responseTransformer(AsyncResponseTransformer.toBytes())
                    .build();

            ResponseBytes<GetObjectResponse> responseBytes = s3TransferManager.download(downloadRequest)
                    .completionFuture()
                    .join()
                    .result();

            return responseBytes.asString(StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Failed to read master.m3u8 from S3 via TransferManager. Key: {}", s3Key, e);
            throw new RuntimeException("Error reading master.m3u8 from S3", e);
        }
    }

    private String rewriteMasterContent(String m3u8Content, String basePath) {
        StringBuilder rewritten = new StringBuilder();
        String[] lines = m3u8Content.split("\r?\n");

        for (String line: lines) {
            String trimmed = line.trim();

            // Skip empty lines and comments
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                rewritten.append(line).append("\n");
                continue;
            }

            // If the line contains sub-playlist (.m3u8)
            if (trimmed.endsWith(".m3u8")) {
                String subPlaylistS3Key = basePath + trimmed;
                // Presign cho sub-playlist này
                String presignedSubPlaylistUrl = generatePresignedUrl(subPlaylistS3Key);
                rewritten.append(presignedSubPlaylistUrl).append("\n");
            } else {
                rewritten.append(line).append("\n");
            }
        }
        return rewritten.toString();
    }

    private String rewriteSubPlaylistContent(String m3u8Content, String segmentFolderPath) {
        StringBuilder rewritten = new StringBuilder();
        String[] lines = m3u8Content.split("\r?\n");

        for (String line : lines) {
            String trimmed = line.trim();

            if (!trimmed.startsWith("#") && trimmed.endsWith(".ts")) {
                String tsS3Key = segmentFolderPath + trimmed;
                String presignedTsUrl = generatePresignedUrl(tsS3Key);
                rewritten.append(presignedTsUrl).append("\n");
            } else {
                rewritten.append(line).append("\n");
            }
        }
        return rewritten.toString();
    }

    private String generatePresignedUrl(String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(presignedUrlExpirySeconds))
                .getObjectRequest(getObjectRequest)
                .build();
        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }
}

// For example master:
//
//#EXTM3U
//
//#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720
//720p/index.m3u8
//
//#EXT-X-STREAM-INF:BANDWIDTH=1400000,RESOLUTION=854x480
//480p/index.m3u8
//
//Spring rewrite to:
//
//#EXTM3U
//
//#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720
//https://s3.../videos/.../720p/index.m3u8?X-Amz-...
//
//#EXT-X-STREAM-INF:BANDWIDTH=1400000,RESOLUTION=854x480
//https://s3.../videos/.../480p/index.m3u8?X-Amz-...
