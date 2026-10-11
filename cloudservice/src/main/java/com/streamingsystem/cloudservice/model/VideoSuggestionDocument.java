package com.streamingsystem.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.util.UUID;

@Document(indexName = "video_suggestions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Mapping(mappingPath = "/elasticsearch/videos-suggestions-mappings.json")
public class VideoSuggestionDocument {

    @Id
    private UUID id;

    @Field(name = "search_term")
    @CompletionField
    private Completion searchTerm;
}
