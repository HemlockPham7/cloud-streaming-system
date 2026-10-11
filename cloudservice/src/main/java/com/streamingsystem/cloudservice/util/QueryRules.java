package com.streamingsystem.cloudservice.util;

import java.util.List;
import java.util.Objects;

public class QueryRules {

    private static final String BOOST_FIELD_FORMAT = "%s^%f";

    private static String boostField(String field, float boost){
        return BOOST_FIELD_FORMAT.formatted(field, boost);
    }

    private static final List<String> SEARCH_BOOST_FIELDS = List.of(
            boostField(Constants.Videos.TITLE, 3.0f),
            boostField(Constants.Videos.DESCRIPTION, 2.0f),
            Constants.Videos.AUTHOR,
            Constants.Videos.CATEGORY
    );

    public static final QueryRule SEARCH_QUERY = QueryRule.of(
            vsr -> Objects.nonNull(vsr.query()),
            vsr -> ElasticsearchUtil.buildMultiMatchQuery(SEARCH_BOOST_FIELDS, vsr.query())
    );

    public static final QueryRule VIEW_COUNT_QUERY = QueryRule.of(
            vsr -> Objects.nonNull(vsr.query()),
            _ -> ElasticsearchUtil.buildRangeQuery(Constants.Videos.VIEW_COUNT, builder -> builder.gte(1000.0))
    );

    public static final QueryRule LIKE_COUNT_QUERY = QueryRule.of(
            vsr -> Objects.nonNull(vsr.query()),
            _ -> ElasticsearchUtil.buildRangeQuery(Constants.Videos.LIKE_COUNT, builder -> builder.gte(100.0))
    );

    public static final QueryRule CATEGORY_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.query()),
            srp -> ElasticsearchUtil.buildTermQuery(Constants.Videos.CATEGORY_RAW, srp.query(), 5.0f)
    );

    public static final QueryRule AUTHOR_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.query()),
            srp -> ElasticsearchUtil.buildTermQuery(Constants.Videos.AUTHOR_RAW, srp.query(), 5.0f)
    );
}
