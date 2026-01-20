package com.template.worker.global.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PartitionTimingListener implements StepExecutionListener {

    private static final String START_TIME = "startTime";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepExecution.getExecutionContext().putLong(START_TIME, System.currentTimeMillis());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long startTime = stepExecution.getExecutionContext().getLong(START_TIME);
        long duration = System.currentTimeMillis() - startTime;

        log.info(
                "[PARTITION] name={} read={} write={} time={}ms",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                duration);
        return stepExecution.getExitStatus();
    }
}
