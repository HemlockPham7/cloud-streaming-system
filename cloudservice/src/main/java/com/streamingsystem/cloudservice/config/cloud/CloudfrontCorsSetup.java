package com.streamingsystem.cloudservice.config.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

@Configuration
public class CloudfrontCorsSetup {

    private static final Logger log = LoggerFactory.getLogger(CloudfrontCorsSetup.class);

    private final CloudFrontClient cloudFrontClient;

    @Value("${app.cloudfront.base-url}")
    private String cloudfrontBaseUrl;

    @Value("${app.cloudfront.distribution-id}")
    private String distributionId;

    // AWS managed policy: CORS-With-Preflight
    private static final String CORS_POLICY_ID = "5cc3b908-e619-4b99-88e5-2cf7f45965bd";

    public CloudfrontCorsSetup(CloudFrontClient cloudFrontClient) {
        this.cloudFrontClient = cloudFrontClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupCors() {
        try {
            GetDistributionConfigResponse response =
                    cloudFrontClient.getDistributionConfig(
                            GetDistributionConfigRequest.builder()
                                    .id(distributionId)
                                    .build()
                    );
            DistributionConfig config = response.distributionConfig();
            String etag = response.eTag();

            DefaultCacheBehavior currentBehavior = config.defaultCacheBehavior();

            if (currentBehavior == null) {
                log.warn(
                        "Distribution {} has no default cache behavior",
                        distributionId
                );
                return;
            }

            if (CORS_POLICY_ID.equals(currentBehavior.responseHeadersPolicyId())) {
                log.info(
                        "CORS response headers policy is already configured"
                );
                return;
            }

            DefaultCacheBehavior updatedBehavior =
                    currentBehavior.toBuilder()
                            .responseHeadersPolicyId(CORS_POLICY_ID)
                            .build();

            DistributionConfig updatedConfig =
                    config.toBuilder()
                            .defaultCacheBehavior(updatedBehavior)
                            .build();

            cloudFrontClient.updateDistribution(
                    UpdateDistributionRequest.builder()
                            .id(distributionId)
                            .ifMatch(etag)
                            .distributionConfig(updatedConfig)
                            .build()
            );

            log.info(
                    "Attached CORS response headers policy to distribution {}",
                    distributionId
            );
        } catch (Exception e) {
            log.error(
                    "Failed to configure CloudFront CORS for distribution {}",
                    distributionId,
                    e
            );
        }
    }
}
