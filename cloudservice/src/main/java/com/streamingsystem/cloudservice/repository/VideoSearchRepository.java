package com.streamingsystem.cloudservice.repository;

import com.streamingsystem.cloudservice.model.VideoSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoSearchRepository extends ElasticsearchRepository<VideoSearchDocument, UUID> {
}
