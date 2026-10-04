package com.streamingsystem.cloudservice.processor.sales;

import com.streamingsystem.cloudservice.dto.SalesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesProcessor implements ItemProcessor<SalesDTO, SalesDTO> {

    @Override
    public SalesDTO process(SalesDTO item) throws Exception {
        log.info("processing the item: {}", item);
        if ("United States".equalsIgnoreCase(item.country())) {
            return null;
        }
        return item;
    }

    // Processor is used for :
    //validate
    //transform data
    //enrich data
    //filter data
    //business logic on each item
}
