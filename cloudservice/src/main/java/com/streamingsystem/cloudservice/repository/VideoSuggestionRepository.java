package com.streamingsystem.cloudservice.repository;

import com.streamingsystem.cloudservice.model.VideoSuggestionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoSuggestionRepository extends ElasticsearchRepository<VideoSuggestionDocument, UUID> {
}
