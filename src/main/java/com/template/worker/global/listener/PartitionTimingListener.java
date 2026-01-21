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
    private static final String HAS_DATA = "HAS_DATA";

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

        return stepExecution.getExitStatus();
    }
}
