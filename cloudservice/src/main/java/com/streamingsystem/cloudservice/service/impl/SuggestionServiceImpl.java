package com.streamingsystem.cloudservice.service.impl;

import com.streamingsystem.cloudservice.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final ElasticsearchOperations elasticsearchOperations;


    @Override
    public List<String> fetchSuggestions(String prefix) {
        return List.of();
    }
}
