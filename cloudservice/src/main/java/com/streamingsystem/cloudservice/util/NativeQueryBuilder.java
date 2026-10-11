package com.streamingsystem.cloudservice.util;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.streamingsystem.cloudservice.dto.video.VideoSearchRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;

import java.util.List;
import java.util.Optional;

public class NativeQueryBuilder {

    private static final List<QueryRule> MUST_QUERY_RULES = List.of(
            QueryRules.SEARCH_QUERY
    );

    private static final List<QueryRule> FILTER_QUERY_RULES = List.of(
            QueryRules.VIEW_COUNT_QUERY,
            QueryRules.LIKE_COUNT_QUERY
    );

    private static final List<QueryRule> SHOULD_QUERY_RULES = List.of(
            QueryRules.CATEGORY_QUERY,
            QueryRules.AUTHOR_QUERY
    );

    public static NativeQuery toSearchQuery(VideoSearchRequest request) {
        List<Query> mustQueries = buildQueries(MUST_QUERY_RULES, request);
        List<Query> filterQueries = buildQueries(FILTER_QUERY_RULES, request);
        List<Query> shouldQueries = buildQueries(SHOULD_QUERY_RULES, request);

        BoolQuery boolQuery = BoolQuery.of(
                builder -> builder.filter(filterQueries).must(mustQueries).should(shouldQueries)
        );

        return NativeQuery.builder()
                .withQuery(Query.of(builder -> builder.bool(boolQuery)))
                .withPageable(PageRequest.of(request.page(), request.size()))
                .withTrackTotalHits(true)
                .build();
    }

    private static List<Query> buildQueries(List<QueryRule> queryRules, VideoSearchRequest request) {
        return queryRules.stream()
                .map(qr -> qr.build(request))
                .flatMap(Optional::stream)
                .toList();
    }

    public static NativeQuery toSuggestQuery(String prefix) {
        Suggester suggester = ElasticsearchUtil.buildCompleteSuggester(
                Constants.Suggestion.SUGGEST_NAME,
                Constants.Suggestion.SEARCH_TERM,
                prefix,
                Constants.Suggestion.DEFAULT_SIZE
        );
        return NativeQuery.builder()
                .withSuggester(suggester)
                .withMaxResults(0)
                .withSourceFilter(FetchSourceFilter.of(b -> b.withExcludes("*")))
                .withSort(SortOptions.of(s -> s
                        .score(score -> score.order(SortOrder.Desc))
                ))
                .build();
    }

}
