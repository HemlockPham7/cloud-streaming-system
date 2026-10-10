package com.streamingsystem.cloudservice.dto.pagination;

import java.io.Serializable;
import java.util.List;

public record GenericPaginationResponse<T> (
        List<T> data,
        PaginationResponse pagination
) implements Serializable {
}