package com.streamingsystem.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(indexName = "videos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSearchDocument {

    @Id
    private UUID id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = @InnerField(
                    suffix = "keyword",
                    type = FieldType.Keyword
            )
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = @InnerField(
                    suffix = "keyword",
                    type = FieldType.Keyword
            )
    )
    private String author;

    @Field(type = FieldType.Keyword)
    private String thumbnailKey;

    @Field(type = FieldType.Keyword)
    private String thumbnailType;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = @InnerField(
                    suffix = "keyword",
                    type = FieldType.Keyword
            )
    )
    private String category;

    @Field(type = FieldType.Long)
    private Long viewCount;

    @Field(type = FieldType.Long)
    private Long likeCount;

    @Field(
            type = FieldType.Date,
            format = {},
            pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSS"
    )
    private LocalDateTime createdAt;
}
