package com.streamingsystem.cloudservice.util;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;

import java.util.List;
import java.util.function.UnaryOperator;

public class ElasticsearchUtil {

    public static Suggester buildCompleteSuggester(String suggestName, String field, String prefix, int limit) {
        SuggestFuzziness suggestFuzziness = SuggestFuzziness.of(
                builder -> builder
                        .fuzziness(Constants.Fuzzy.LEVEL)
                        .prefixLength(Constants.Fuzzy.PREFIX_LENGTH)
        );
        CompletionSuggester completionSuggester = CompletionSuggester.of(
                builder -> builder
                        .field(field)
                        .size(limit)
                        .fuzzy(suggestFuzziness)
                        .skipDuplicates(true)
        );
        FieldSuggester fieldSuggester = FieldSuggester.of(
                builder -> builder
                        .prefix(prefix)
                        .completion(completionSuggester)
        );
        return Suggester.of(builder -> builder.suggesters(suggestName, fieldSuggester));
    }

    public static Query buildMultiMatchQuery(List<String> fields, String searchTerm) {
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(
                builder -> builder.query(searchTerm)
                        .fields(fields)
                        .fuzziness(Constants.Fuzzy.LEVEL)
                        .prefixLength(Constants.Fuzzy.PREFIX_LENGTH)
                        .type(TextQueryType.MostFields)
                        .operator(Operator.And)
        );
        return Query.of(builder -> builder.multiMatch(multiMatchQuery));
    }

    public static Query buildRangeQuery(String field, UnaryOperator<NumberRangeQuery.Builder> function) {
        NumberRangeQuery numberRangeQuery = NumberRangeQuery.of(
                builder -> function.apply(builder.field(field))
        );
        RangeQuery rangeQuery = RangeQuery.of(
                builder -> builder.number(numberRangeQuery)
        );
        return Query.of(builder -> builder.range(rangeQuery));
    }

    public static Query buildTermQuery(String field, String value, float boost) {
        TermQuery termQuery = TermQuery.of(
                builder -> builder.field(field)
                        .value(value)
                        .boost(boost)
                        .caseInsensitive(true)
        );
        return Query.of(builder -> builder.term(termQuery));
    }
}
