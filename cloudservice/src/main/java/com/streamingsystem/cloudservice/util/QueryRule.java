package com.streamingsystem.cloudservice.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.streamingsystem.cloudservice.dto.video.VideoSearchRequest;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class QueryRule {

    private final Predicate<VideoSearchRequest> predicate;
    private final Function<VideoSearchRequest, Query> function;

    public QueryRule(
            Predicate<VideoSearchRequest> predicate,
            Function<VideoSearchRequest, Query> function
    ) {
        this.predicate = predicate;
        this.function = function;
    }

    public static QueryRule of(
            Predicate<VideoSearchRequest> predicate,
            Function<VideoSearchRequest, Query> function
    ) {
        return new QueryRule(predicate, function);
    }

    public Optional<Query> build(VideoSearchRequest parameters) {
        if (predicate.test(parameters)) {
            return Optional.ofNullable(function.apply(parameters));
        }

        return Optional.empty();
    }
}
