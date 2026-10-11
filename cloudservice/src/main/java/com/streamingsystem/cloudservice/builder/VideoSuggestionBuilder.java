package com.streamingsystem.cloudservice.builder;

import org.springframework.stereotype.Component;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class VideoSuggestionBuilder {

    public Completion build(String title, String author, String category) {
        Set<String> terms = new LinkedHashSet<>();

        addTitleSuggestions(terms, title);
        addIfPresent(terms, author);
        addIfPresent(terms, category);

        return new Completion(terms.toArray(new String[0]));
    }

    private void addTitleSuggestions(Set<String> terms, String title) {
        if (title == null || title.isBlank()) {
            return;
        }

        String normalizedTitle = title.trim().replaceAll("\\s+", " ");
        terms.add(normalizedTitle);

        String[] words = normalizedTitle.split("\\s+");

        for (int i = 1; i < words.length; i++) {
            terms.add(String.join(
                    " ",
                    Arrays.copyOfRange(words, 0, i + 1)
            ));
        }
    }

    private void addIfPresent(Set<String> terms, String value) {
        if (value != null && !value.isBlank()) {
            terms.add(value.trim());
        }
    }
}
