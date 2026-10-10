package com.streamingsystem.cloudservice.service;

import java.util.List;

public interface SuggestionService {

    List<String> fetchSuggestions(String prefix);
}
