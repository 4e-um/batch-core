package com.template.worker.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job {} is starting...", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchStatus status = jobExecution.getStatus();
        log.info(
                "Job {} finished with status: {}",
                jobExecution.getJobInstance().getJobName(),
                status);

        if (status == BatchStatus.COMPLETED) {
            log.info("Health check job completed successfully");
        } else {
            log.error("Health check job failed with status: {}", status);
        }
    }
}
