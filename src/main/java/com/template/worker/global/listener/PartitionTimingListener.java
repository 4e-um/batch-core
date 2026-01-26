package com.template.worker.global.listener;

import java.time.Duration;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartitionTimingListener implements StepExecutionListener {

    private static final String START_TIME = "startTime";
    private static final String HAS_DATA = "HAS_DATA";

    private final MeterRegistry meterRegistry;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepExecution.getExecutionContext().putLong(START_TIME, System.currentTimeMillis());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long startTime = stepExecution.getExecutionContext().getLong(START_TIME);
        long duration = System.currentTimeMillis() - startTime;

        long readCount = stepExecution.getReadCount();
        long writeCount = stepExecution.getWriteCount();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();

        log.info(
                "[PARTITION] name={} read={} write={} time={}ms",
                stepExecution.getStepName(),
                readCount,
                writeCount,
                duration);

        // ✅ 이 partition에서 데이터가 하나라도 있으면 JobExecution에 표시
        if (readCount > 0) {
            stepExecution.getJobExecution().getExecutionContext().put(HAS_DATA, true);
        }

        // Record Prometheus metrics with job_name tag
        if (stepExecution.getStatus() == BatchStatus.COMPLETED) {
            meterRegistry
                    .counter(
                            "spring.batch.partition.count",
                            "status",
                            "COMPLETED",
                            "job_name",
                            jobName)
                    .increment();
            log.info("[METRICS] partitionCompletedCounter incremented for job={}", jobName);
        } else if (stepExecution.getStatus() == BatchStatus.FAILED) {
            meterRegistry
                    .counter(
                            "spring.batch.partition.count", "status", "FAILED", "job_name", jobName)
                    .increment();
            log.info("[METRICS] partitionFailedCounter incremented for job={}", jobName);
        }

        meterRegistry
                .counter("spring.batch.step.item.count", "job_name", jobName)
                .increment(readCount);

        meterRegistry
                .timer("spring.batch.step.duration", "job_name", jobName)
                .record(Duration.ofMillis(duration));

        log.info(
                "[METRICS] stepItemCounter +{}, stepDurationTimer {}ms for job={}",
                readCount,
                duration,
                jobName);

        return stepExecution.getExitStatus();
    }
}
