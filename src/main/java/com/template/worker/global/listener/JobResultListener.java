package com.template.worker.global.listener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JobResultListener {

    private static final String HAS_DATA = "HAS_DATA";
    private static final ExitStatus NO_DATA_EXIT_STATUS = new ExitStatus("NO_DATA", "⚠ 데이터가 없습니다.");

    private final JobLogger jobLogger;
    private final AtomicInteger activeJobsGauge;
    private final MeterRegistry meterRegistry;

    @BeforeJob
    public void before(JobExecution jobExecution) {
        activeJobsGauge.incrementAndGet();
    }

    @AfterJob
    public void after(JobExecution jobExecution) {
        activeJobsGauge.decrementAndGet();

        ExecutionContext context = jobExecution.getExecutionContext();
        String jobName = jobExecution.getJobInstance().getJobName();

        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime();
        long duration =
                (start != null && end != null) ? Duration.between(start, end).toMillis() : 0L;

        boolean hasData =
                context.containsKey(HAS_DATA) && Boolean.TRUE.equals(context.get(HAS_DATA));

        // ✅ partition 전체에서 데이터가 하나도 없었던 경우 → 실패
        if (!hasData) {
            jobExecution.setStatus(BatchStatus.FAILED);
            jobExecution.setExitStatus(NO_DATA_EXIT_STATUS);
            meterRegistry.counter("spring.batch.job.failed.total", "job_name", jobName).increment();

            jobLogger.jobFailed(
                    jobName,
                    duration,
                    new IllegalStateException(NO_DATA_EXIT_STATUS.getExitDescription()));
            return;
        }

        // 기존 성공/실패 로직 유지
        if (jobExecution.getStatus().isUnsuccessful()) {
            Throwable cause =
                    jobExecution.getAllFailureExceptions().isEmpty()
                            ? null
                            : jobExecution.getAllFailureExceptions().get(0);
            meterRegistry.counter("spring.batch.job.failed.total", "job_name", jobName).increment();

            jobLogger.jobFailed(jobName, duration, cause);
        } else {
            meterRegistry
                    .counter("spring.batch.job.completed.total", "job_name", jobName)
                    .increment();
            jobLogger.jobSuccess(jobName, duration);
        }
    }
}
