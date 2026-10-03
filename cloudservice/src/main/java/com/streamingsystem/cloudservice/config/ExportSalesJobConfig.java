package com.streamingsystem.cloudservice.config;

import com.streamingsystem.cloudservice.dto.SalesDTO;
import com.streamingsystem.cloudservice.processor.SalesProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ExportSalesJobConfig {

    private static final String JOB_NAME = "ExportDataFromDBToFileJob";
    private static final String STEP_NAME = "fromSalesTableToFile";

    private static final int CHUNK_SIZE = 10;

    private static final String SELECT_CLAUSE = """
            SELECT
                sale_id,
                product_id,
                customer_id,
                sale_date,
                sale_amount,
                store_location,
                country
            FROM sales
            WHERE processed = false
            """;

    private final DataSource dataSource;
    private final SalesProcessor processor;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dbToFileJob(Step fromSalesTableToFile) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fromSalesTableToFile)
                .build();
    }

    @Bean
    public Step fromSalesTableToFile(FlatFileItemWriter<SalesDTO> flatFileItemWriter) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<SalesDTO, SalesDTO>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(salesDTOJdbcCursorItemReader())
                .processor(processor)
                .writer(flatFileItemWriter)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<SalesDTO> salesDTOJdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<SalesDTO>()
                .name("salesReader")
                .dataSource(dataSource)
                .sql(SELECT_CLAUSE)
                .fetchSize(CHUNK_SIZE)
                .rowMapper(new DataClassRowMapper<>(SalesDTO.class))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<SalesDTO> flatFileItemWriter(@Value("#{jobParameters['output.file.name']}") String outputFile) {
        return new FlatFileItemWriterBuilder<SalesDTO>()
                .name("salesFileWriter")
                .resource(new FileSystemResource(outputFile))
                .headerCallback(writer -> writer.write(
                        "productId;customerId;saleDate;saleAmount;storeLocation;country"
                ))
                .delimited()
                .delimiter(";")
                .sourceType(SalesDTO.class)
                .names(
                        "productId",
                        "customerId",
                        "saleDate",
                        "saleAmount",
                        "storeLocation",
                        "country"
                )
                .shouldDeleteIfEmpty(Boolean.FALSE)
                .append(Boolean.TRUE)
                .build();
    }
}
