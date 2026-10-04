package com.streamingsystem.cloudservice.scheduler.sales;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesJobScheduler {

    private final Job dbToFileJob;
    private final JobOperator jobOperator;

    @Scheduled(cron = "0 0 23 * * *")
    public void trigger() throws Exception {

        String fileName = LocalDate.now()
                .toString()
                .concat("_sales.csv");

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("output.file.name", fileName)
                .addDate("run.timestamp", new Date())
                .toJobParameters();

        log.info("Job parameters before start: {}", jobParameters);

        JobExecution jobExecution = jobOperator.start(dbToFileJob, jobParameters);

        log.info("Started job: {}, executionId: {}", dbToFileJob.getName(), jobExecution.getId());
        log.info("Job execution parameters: {}", jobExecution.getJobParameters());
    }
}
