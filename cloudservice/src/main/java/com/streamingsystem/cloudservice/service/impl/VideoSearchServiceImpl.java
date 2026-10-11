package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.builder.VideoSuggestionBuilder;
import com.streamingsystem.cloudservice.dto.pagination.GenericPaginationResponse;
import com.streamingsystem.cloudservice.dto.pagination.PaginationResponse;
import com.streamingsystem.cloudservice.dto.video.VideoSearchRequest;
import com.streamingsystem.cloudservice.dto.video.VideoSearchResponse;
import com.streamingsystem.cloudservice.event.dto.VideoReadyEvent;
import com.streamingsystem.cloudservice.model.VideoSearchDocument;
import com.streamingsystem.cloudservice.model.VideoSuggestionDocument;
import com.streamingsystem.cloudservice.repository.VideoSearchRepository;
import com.streamingsystem.cloudservice.repository.VideoSuggestionRepository;
import com.streamingsystem.cloudservice.service.VideoSearchService;
import com.streamingsystem.cloudservice.util.Constants;
import com.streamingsystem.cloudservice.util.NativeQueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VideoSearchServiceImpl implements VideoSearchService {

    @Value("${spring.cloud.aws.bucket.streaming.name}")
    private String bucket;

    private final S3Presigner s3Presigner;

    private final VideoSearchRepository videoSearchRepository;
    private final VideoSuggestionRepository videoSuggestionRepository;
    private final VideoSuggestionBuilder videoSuggestionBuilder;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional(readOnly = true)
    @Override
    public void indexVideo(VideoReadyEvent event) {
        Completion suggestions = videoSuggestionBuilder.build(
                event.title(),
                event.author(),
                event.category()
        );
        VideoSuggestionDocument videoSuggestionDocument = VideoSuggestionDocument.builder()
                .id(event.videoId())
                .searchTerm(suggestions)
                .build();
        videoSuggestionRepository.save(videoSuggestionDocument);

        VideoSearchDocument videoSearchDocument = VideoSearchDocument.builder()
                .id(event.videoId())
                .title(event.title())
                .description(event.description())
                .author(event.author())
                .category(event.category())
                .thumbnailKey(event.thumbnailKey())
                .thumbnailType(event.thumbnailType())
                .viewCount(ThreadLocalRandom.current().nextLong(1, 10_000_001))
                .likeCount(ThreadLocalRandom.current().nextLong(1, 10_000_001))
                .createdAt(event.createdAt())
                .build();
        videoSearchRepository.save(videoSearchDocument);
    }

    @Override
    public List<String> fetchSuggestions(String prefix) {
        NativeQuery query = NativeQueryBuilder.toSuggestQuery(prefix);
        SearchHits<VideoSuggestionDocument> searchHits = elasticsearchOperations.search(
                query,
                VideoSuggestionDocument.class,
                Constants.Index.VIDEOS_SUGGESTIONS
        );
        if (searchHits.getSuggest() == null) {
            return Collections.emptyList();
        }
        return searchHits.getSuggest().getSuggestions().stream()
                .flatMap(entry -> entry.getEntries().stream())
                .flatMap(line -> line.getOptions().stream())
                .map(Suggest.Suggestion.Entry.Option::getText)
                .toList();
    }

    @Override
    public GenericPaginationResponse<VideoSearchResponse> searchVideos(VideoSearchRequest request) {

        NativeQuery query = NativeQueryBuilder.toSearchQuery(request);
        SearchHits<VideoSearchDocument> searchHits = elasticsearchOperations.search(
                query,
                VideoSearchDocument.class,
                Constants.Index.VIDEOS
        );
        List<VideoSearchResponse> results = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .map(this::mapToResponse)
                .toList();

        SearchPage<VideoSearchDocument> searchPage = SearchHitSupport
                .searchPageFor(searchHits, PageRequest.of(request.page(), request.size()));
        PaginationResponse paginationResponse = new PaginationResponse(
                searchPage.getNumber(),
                searchPage.getNumberOfElements(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );
        return new GenericPaginationResponse<>(
                results,
                paginationResponse
        );
    }

    private VideoSearchResponse mapToResponse(VideoSearchDocument video) {
        return VideoSearchResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .author(video.getAuthor())
                .thumbnailKey(
                        video.getThumbnailKey() != null
                                ? generateThumbnailPresignedUrl(video.getThumbnailKey())
                                : null
                )
                .thumbnailType(video.getThumbnailType())
                .category(video.getCategory())
                .viewCount(video.getViewCount())
                .likeCount(video.getLikeCount())
                .createdAt(video.getCreatedAt())
                .build();
    }

    private String generateThumbnailPresignedUrl(String thumbnailKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(thumbnailKey)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();
        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }
}
