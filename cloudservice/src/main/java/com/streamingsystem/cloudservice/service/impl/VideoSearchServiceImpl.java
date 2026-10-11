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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VideoSearchServiceImpl implements VideoSearchService {

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
    public GenericPaginationResponse<VideoSearchDocument> searchVideos(VideoSearchRequest request) {

        NativeQuery query = NativeQueryBuilder.toSearchQuery(request);
        SearchHits<VideoSearchDocument> searchHits = elasticsearchOperations.search(
                query,
                VideoSearchDocument.class,
                Constants.Index.VIDEOS
        );
        List<VideoSearchDocument> results = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();
        SearchPage<VideoSearchDocument> searchPage = SearchHitSupport
                .searchPageFor(searchHits, PageRequest.of(request.page(), request.size()));

        PaginationResponse paginationResponse = new PaginationResponse(
                searchPage.getNumber(),
                searchPage.getNumberOfElements(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );
        return new GenericPaginationResponse<VideoSearchDocument>(
                results,
                paginationResponse
        );
    }
}
