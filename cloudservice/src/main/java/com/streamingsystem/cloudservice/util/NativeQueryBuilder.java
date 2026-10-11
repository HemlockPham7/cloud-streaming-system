package com.streamingsystem.cloudservice.util;

import co.elastic.clients.elasticsearch.core.search.Suggester;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;

public class NativeQueryBuilder {

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
                .build();
    }
}
