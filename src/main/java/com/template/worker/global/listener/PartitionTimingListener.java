package com.template.worker.global.listener;

import java.time.Duration;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartitionTimingListener implements StepExecutionListener {

    private static final String START_TIME = "startTime";
    private static final String HAS_DATA = "HAS_DATA";

    private final Counter partitionCompletedCounter;
    private final Counter partitionFailedCounter;
    private final Counter stepItemCounter;
    private final Timer stepDurationTimer;

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

        // Record Prometheus metrics
        if (stepExecution.getStatus() == BatchStatus.COMPLETED) {
            partitionCompletedCounter.increment();
            log.info("[METRICS] partitionCompletedCounter incremented");
        } else if (stepExecution.getStatus() == BatchStatus.FAILED) {
            partitionFailedCounter.increment();
            log.info("[METRICS] partitionFailedCounter incremented");
        }
        stepItemCounter.increment(readCount);
        stepDurationTimer.record(Duration.ofMillis(duration));
        log.info("[METRICS] stepItemCounter +{}, stepDurationTimer {}ms", readCount, duration);

        return stepExecution.getExitStatus();
    }
}
