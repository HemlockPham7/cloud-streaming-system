package com.streamingsystem.cloudservice.scheduler.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessUploadImageJobScheduler {

    private final Job migrateProcessImagesToS3Job;
    private final JobOperator jobOperator;

    @Scheduled(cron = "0 0 23 * * *")
    public void trigger() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("run.timestamp", new Date())
                .toJobParameters();

        log.info("Job parameters before start: {}", jobParameters);
        jobOperator.start(migrateProcessImagesToS3Job, jobParameters);
    }
}
