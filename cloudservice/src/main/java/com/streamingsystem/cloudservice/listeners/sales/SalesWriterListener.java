package com.streamingsystem.cloudservice.listeners.sales;


import com.streamingsystem.cloudservice.dto.SalesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SalesWriterListener implements ItemWriteListener<SalesDTO> {

    private static final String UPDATE_PROCESSED_SQL = """
            UPDATE sales
            SET processed = TRUE
            WHERE sale_id IN (:ids)
            """;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void afterWrite(Chunk<? extends SalesDTO> items) {
        List<Long> itemWrittenIds = items.getItems()
                .stream()
                .map(SalesDTO::saleId)
                .toList();

        if (itemWrittenIds.isEmpty()) {
            log.info("No rows updated!!!!!!!!");
            return;
        }

        updateProcessedRecords(itemWrittenIds);
    }


    private void updateProcessedRecords(List<Long> saleIds) {
        int totalRowsAffected = namedParameterJdbcTemplate.update(
                UPDATE_PROCESSED_SQL,
                new MapSqlParameterSource("ids", saleIds)
        );

        log.info("Total rows exported: {}", totalRowsAffected);
    }
}
