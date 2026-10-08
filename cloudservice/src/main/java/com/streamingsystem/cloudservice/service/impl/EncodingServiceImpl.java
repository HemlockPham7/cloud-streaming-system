package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.event.dto.VideoEncodedEvent;
import com.streamingsystem.cloudservice.event.dto.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.streamingsystem.cloudservice.dto.video.VideoQuality;
import com.streamingsystem.cloudservice.service.EncodingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedDirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EncodingServiceImpl implements EncodingService {

    public static final String VIDEO_ENCODED_TOPIC = "video.encoded";

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    private static final List<VideoQuality> VIDEO_QUALITIES = List.of(
            new VideoQuality(1280, 720, 2800), // 720 - 2800k bitrate
            new VideoQuality(854, 480, 1200)   // 480 - 1200k bitrate
    );

    private final S3TransferManager s3TransferManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     *  Main encoding pipeline
     *  Steps:
     *  1. Download raw video from s3
     *  2. encode to multiple qualities using FFmpeg
     *  3. Generate HLS playlist (.m3u8) for each quality.
     *  4. Create master playlist
     *  5. Upload all encoded files back to s3.
     *  6. Publish VideoEncodedEvent to Kafka
     */
    @Override
    public void encodeVideo(VideoUploadedEvent event) {
        Path tempDir = null;
        try {
            // Step 1
            tempDir = Files.createTempDirectory("video_transcode_" + event.videoId());
            Path rawVideoPath = tempDir.resolve("original.mp4");
            Path hlsOutputDir = tempDir.resolve("hls");
            Files.createDirectories(hlsOutputDir);

            // Step 2
            log.info("Downloading raw video from S3: bucket={}, key={}", event.bucket(), event.originalKey());
            downloadVideoFromS3(event.bucket(), event.originalKey(), rawVideoPath);

            // Step 3
            List<VideoEncodedEvent.RenditionInfo> renditions = new ArrayList<>();

            for (VideoQuality quality : VIDEO_QUALITIES) {
                Path qualityDir = hlsOutputDir.resolve(quality.getResolutionName());
                Files.createDirectories(qualityDir);

                log.info("Encoding quality {} for videoId: {}", quality.getResolutionName(), event.videoId());
                encodeToHLS(rawVideoPath.toString(), qualityDir.toString(), quality.width(), quality.height(), quality.bitrateKbps());

                String playlistKey = String.format("videos/%s/hls/%s/index.m3u8", event.videoId(), quality.getResolutionName());
                renditions.add(new VideoEncodedEvent.RenditionInfo(
                        quality.getResolutionName(),
                        quality.width(),
                        quality.height(),
                        (long) quality.bitrateKbps() * 1000,
                        playlistKey
                ));
            }

            // Step 4
            Path masterPlaylistPath = hlsOutputDir.resolve("master.m3u8");
            generateMasterPlaylist(masterPlaylistPath.toString());
            String hlsMasterKey = String.format("videos/%s/hls/master.m3u8", event.videoId());

            // Step 5
            String s3Prefix = String.format("videos/%s/hls", event.videoId());
            log.info("Uploading encoded HLS directory to S3 prefix: {}", s3Prefix);
            uploadDirectoryToS3(event.bucket(), s3Prefix, hlsOutputDir);

            // Step 6
            VideoEncodedEvent encodedEvent = new VideoEncodedEvent(
                    event.videoId(),
                    hlsMasterKey,
                    renditions
            );

            kafkaTemplate.send(VIDEO_ENCODED_TOPIC, event.videoId().toString(), encodedEvent)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish VideoEncodedEvent for videoId: {}", event.videoId(), ex);
                        } else {
                            log.info("Successfully published VideoEncodedEvent for videoId: {}", event.videoId());
                        }
                    });

        } catch (Exception e) {
            log.error("Failed to encode video for videoId: {}", event.videoId(), e);
            throw new RuntimeException("Encoding pipeline failed", e);
        } finally {
            cleanUpTempFolder(tempDir);
        }
    }

    private void downloadVideoFromS3(String bucket, String key, Path targetPath) {
        CompletedFileDownload download = s3TransferManager.downloadFile(builder -> builder
                .getObjectRequest(b -> b.bucket(bucket).key(key))
                .destination(targetPath)
        ).completionFuture().join();

        log.info("Raw video downloaded successfully to local path: {}", targetPath);
    }

    /**
     * Encode video to HLS using FFmpeg.
     *
     * FFmpeg command created:
     * - Multiple .ts segment files (10 seconds each)
     * - A .m3u8 playlist file for this quality
     *
     * @param inputPath
     * @param outputDir
     * @param width
     * @param height
     * @param bitrateKbps
     * @throws IOException
     * @throws InterruptedException
     */
    private void encodeToHLS(
            String inputPath,
            String outputDir,
            int width, int height, int bitrateKbps
    ) throws IOException, InterruptedException {

        String playlistPath = outputDir + File.separator + "index.m3u8";
        String segmentPattern = outputDir + File.separator + "segment_%03d.ts";

        // FFmpeg Command for HLS encoding
        List<String> command = Arrays.asList(
                ffmpegPath,
                "-y",                                          // Overwrite output files if exists
                "-i", inputPath,                               // Input file
                "-vf", "scale=" + width + ":" + height,        // Resize video
                "-c:v", "libx264",                             // video codec
                "-b:v", bitrateKbps + "k",                     // video bitrateKbps
                "-c:a", "aac",                                 // audio coded
                "-b:a", "128k",                                // audio bitrate
                "-hls_time", "10",                             // 10 second segments
                "-hls_list_size", "0",                         // keep all segments
                "-hls_segment_filename", segmentPattern,       // segment naming
                "-f", "hls",                                   // output format HLS
                playlistPath                                   // output playlist
        );

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg encoding failed with exit code: " + exitCode);
        }
    }

    /**
     * Generate master HLS playlist that references all quality playlists.
     * This is the file the video player download first.
     * @param masterPlaylistPath
     * @throws IOException
     */
    private void generateMasterPlaylist(String masterPlaylistPath) throws IOException {
        StringBuilder master = new StringBuilder();
        master.append("#EXTM3U\n");
        master.append("#EXT-X-VERSION:3\n\n");

        for (VideoQuality videoQuality : VIDEO_QUALITIES) {
            master.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                    .append(videoQuality.bitrateKbps() * 1000)
                    .append(",RESOLUTION=")
                    .append(videoQuality.width()).append("x").append(videoQuality.height())
                    .append(",CODECS=\"avc1.42e01e,mp4a.40.2\"\n");
            master.append(videoQuality.getResolutionName()).append("/index.m3u8\n\n");
        }

        Files.writeString(Paths.get(masterPlaylistPath), master.toString());
    }

    private void uploadDirectoryToS3(String bucket, String s3Prefix, Path localDirectory) {
        DirectoryUpload directoryUpload = s3TransferManager.uploadDirectory(builder -> builder
                .bucket(bucket)
                .s3Prefix(s3Prefix)
                .source(localDirectory)
        );

        CompletedDirectoryUpload completedUpload = directoryUpload.completionFuture().join();
        if (!completedUpload.failedTransfers().isEmpty()) {
            log.error("Failed to upload some HLS files to S3: {}", completedUpload.failedTransfers());
            throw new RuntimeException("Failed to upload all HLS files to S3");
        }
        log.info("HLS directory uploaded successfully to S3 prefix: {}", s3Prefix);
    }

    private void cleanUpTempFolder(Path tempDir) {
        if (tempDir == null || !Files.exists(tempDir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(tempDir)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            log.info("Temporary directory cleaned up: {}", tempDir);
        } catch (IOException e) {
            log.error("Failed to clean up temporary directory: {}", tempDir, e);
        }
    }
}

// master.m3u8
//#EXTM3U
//#EXT-X-VERSION:3
//
//#EXT-X-STREAM-INF:BANDWIDTH=720000,RESOLUTION=1280x2800,CODECS="avc1.42e01e,mp4a.40.2"
//720p/index.m3u8
//
//#EXT-X-STREAM-INF:BANDWIDTH=480000,RESOLUTION=854x1200,CODECS="avc1.42e01e,mp4a.40.2"
//480p/index.m3u8
// ----------------------
// index.m3u8
//#EXTM3U
//#EXT-X-VERSION:3
//#EXT-X-TARGETDURATION:17
//#EXT-X-MEDIA-SEQUENCE:0
//#EXTINF:16.666667,
//segment_000.ts
//#EXTINF:6.400000,
//segment_001.ts
//#EXT-X-ENDLIST