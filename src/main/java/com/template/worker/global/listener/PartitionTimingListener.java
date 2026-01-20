package com.template.worker.global.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PartitionTimingListener implements StepExecutionListener {

    private long start;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        start = System.currentTimeMillis();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long duration = System.currentTimeMillis() - start;

        log.info(
                "[PARTITION] name={} read={} write={} time={}ms",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                duration
        );
        return stepExecution.getExitStatus();
    }
}