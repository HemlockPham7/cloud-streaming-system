package com.streamingsystem.cloudservice.util;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

public final class Constants {

    private Constants() {}

    public static final class Index {
        public static final IndexCoordinates VIDEOS_SUGGESTIONS = IndexCoordinates.of("video_suggestions");
        public static final IndexCoordinates VIDEOS = IndexCoordinates.of("videos");
    }

    public static final class Suggestion {
        public static final String SEARCH_TERM = "search_term";
        public static final String SUGGEST_NAME = "video-suggest";
        public static final int DEFAULT_SIZE = 8;
    }

    public static final class Fuzzy {
        public static final String LEVEL = "2";
        public static final int PREFIX_LENGTH = 2;
    }

    public static final class Videos {
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String AUTHOR = "author";
        public static final String CATEGORY = "category";
        public static final String VIEW_COUNT = "view_count";
        public static final String LIKE_COUNT = "like_count";
        public static final String THUMBNAIL_KEY = "thumbnail_key";
        public static final String THUMBNAIL_TYPE = "thumbnail_type";
        public static final String CREATED_AT = "created_at";
    }
}
