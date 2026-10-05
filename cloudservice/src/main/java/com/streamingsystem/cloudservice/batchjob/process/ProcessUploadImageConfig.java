package com.streamingsystem.cloudservice.batchjob.process;

import com.streamingsystem.cloudservice.dto.job.ProcessImageMigrationCleanUpDTO;
import com.streamingsystem.cloudservice.dto.job.ProcessImageMigrationDTO;
import com.streamingsystem.cloudservice.dto.job.ProcessImageMigrationResult;
import com.streamingsystem.cloudservice.processor.process.ProcessImageMigrationProcessor;
import com.streamingsystem.cloudservice.writer.process.ProcessImageCleanUpWriter;
import com.streamingsystem.cloudservice.writer.process.ProcessImageMigrationWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.PagingQueryProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.infrastructure.support.DatabaseType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ProcessUploadImageConfig {

    private static final String JOB_NAME = "MigrateProcessImagesToS3Job";
    private static final String STEP_NAME_1 = "migrateProcessImageToS3";
    private static final String STEP_NAME_2 = "cleanupMigratedProcessImages";

    private static final int CHUNK_SIZE = 100;

    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ProcessImageMigrationProcessor processImageMigrationProcessor;

    private final ProcessImageMigrationWriter processImageMigrationWriter;
    private final ProcessImageCleanUpWriter processImageCleanUpWriter;

    @Bean
    public Job migrateProcessImagesToS3Job(Step migrateProcessImageToS3, Step cleanupMigratedProcessImages) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(migrateProcessImageToS3)
                .next(cleanupMigratedProcessImages)
                .build();
    }

    @Bean
    public Step migrateProcessImageToS3(JdbcPagingItemReader<ProcessImageMigrationDTO> processImageMigrationReader) {
        return new StepBuilder(STEP_NAME_1, jobRepository)
                .<ProcessImageMigrationDTO, ProcessImageMigrationResult>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(processImageMigrationReader)
                .processor(processImageMigrationProcessor)
                .writer(processImageMigrationWriter)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<ProcessImageMigrationDTO> processImageMigrationReader(
            PagingQueryProvider processImageMigrationQueryProvider
    ) throws Exception {

        return new JdbcPagingItemReaderBuilder<ProcessImageMigrationDTO>()
                .name("processImageMigrationReader")
                .dataSource(dataSource)
                .queryProvider(processImageMigrationQueryProvider)
                .rowMapper(new DataClassRowMapper<>(ProcessImageMigrationDTO.class))
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean processImageMigrationQueryProvider() {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();

        queryProvider.setSelectClause("""
            SELECT p.id AS processId,
                   p.image_data AS imageData,
                   p.image_file_name AS imageFileName,
                   p.image_content_type AS imageContentType,
                   p.image_size AS imageSize,
                   p.status
            """);

        queryProvider.setFromClause("""
            FROM process p
            LEFT JOIN process_s3_migration m
                   ON p.id = m.process_id
            """);

        queryProvider.setWhereClause("""
            WHERE p.status IN ('COMPLETED', 'CANCELLED')
              AND p.image_data IS NOT NULL
              AND m.process_id IS NULL
            """);

        queryProvider.setDataSource(dataSource);
        queryProvider.setDatabaseType(DatabaseType.POSTGRES.name());
        queryProvider.setSortKeys(Collections.singletonMap("processId", Order.ASCENDING));
        return queryProvider;
    }

    @Bean
    public Step cleanupMigratedProcessImages(JdbcPagingItemReader<ProcessImageMigrationCleanUpDTO> cleanupProcessImageReader) {
        return new StepBuilder(STEP_NAME_2, jobRepository)
                .<ProcessImageMigrationCleanUpDTO, ProcessImageMigrationCleanUpDTO>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(cleanupProcessImageReader)
                .writer(processImageCleanUpWriter)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<ProcessImageMigrationCleanUpDTO> cleanupProcessImageReader(
            PagingQueryProvider cleanupProcessImageQueryProvider
    ) throws Exception {

        return new JdbcPagingItemReaderBuilder<ProcessImageMigrationCleanUpDTO>()
                .name("cleanupProcessImageReader")
                .dataSource(dataSource)
                .queryProvider(cleanupProcessImageQueryProvider)
                .rowMapper(new DataClassRowMapper<>(
                        ProcessImageMigrationCleanUpDTO.class
                ))
                .pageSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean cleanupProcessImageQueryProvider() {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();

        queryProvider.setSelectClause("""
            SELECT p.id AS processId
            """);

        queryProvider.setFromClause("""
            FROM process p
            INNER JOIN process_s3_migration m
                    ON m.process_id = p.id
            """);

        queryProvider.setWhereClause("""
            WHERE p.status IN ('COMPLETED', 'CANCELLED')
              AND p.image_data IS NOT NULL
            """);

        queryProvider.setDataSource(dataSource);
        queryProvider.setDatabaseType(DatabaseType.POSTGRES.name());
        queryProvider.setSortKeys(
                Collections.singletonMap("processId", Order.ASCENDING)
        );
        return queryProvider;
    }
}
