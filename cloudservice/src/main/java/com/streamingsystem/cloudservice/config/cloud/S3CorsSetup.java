package com.streamingsystem.cloudservice.config.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CORSConfiguration;
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;

import java.util.Arrays;

@Configuration
public class S3CorsSetup {

    private final S3AsyncClient s3AsyncClient;

    @Value("${spring.cloud.aws.bucket.streaming.name}")
    private String bucketName;

    public S3CorsSetup(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupCorsOnStartup() {
        CORSRule corsRule = CORSRule.builder()
                .allowedOrigins(Arrays.asList("http://localhost:5173"))
                .allowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "HEAD"))
                .allowedHeaders(Arrays.asList("*"))
                .exposeHeaders(Arrays.asList(
                        "Content-Length",
                        "Content-Range",
                        "Content-Type",
                        "ETag",
                        "Accept-Ranges"
                ))
                .maxAgeSeconds(3000)
                .build();

        CORSConfiguration corsConfiguration = CORSConfiguration.builder()
                .corsRules(Arrays.asList(corsRule))
                .build();

        PutBucketCorsRequest putBucketCorsRequest = PutBucketCorsRequest.builder()
                .bucket(bucketName)
                .corsConfiguration(corsConfiguration)
                .build();

        s3AsyncClient.putBucketCors(putBucketCorsRequest)
                .thenRun(() -> System.out.println("Successfully configured CORS for bucket: " + bucketName + " on S3/LocalStack!"))
                .exceptionally(throwable -> {
                    System.err.println("Error when configuring CORS for bucket: " + throwable.getMessage());
                    return null;
                });
    }
}
