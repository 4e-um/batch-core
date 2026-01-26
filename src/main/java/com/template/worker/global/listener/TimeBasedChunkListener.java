package com.template.worker.global.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimeBasedChunkListener implements ChunkListener {

    private static final long LOG_INTERVAL_MS = 10_000;
    private static final String LAST_LOG_TIME_KEY = "lastLogTime";

    private final Counter chunkCounter;

    @Override
    public void afterChunk(ChunkContext context) {
        StepExecution stepExecution = context.getStepContext().getStepExecution();
        ExecutionContext executionContext = stepExecution.getExecutionContext();

        // Record chunk metric
        chunkCounter.increment();

        long now = System.currentTimeMillis();
        long lastLogTime =
                executionContext.containsKey(LAST_LOG_TIME_KEY)
                        ? executionContext.getLong(LAST_LOG_TIME_KEY)
                        : 0L;

        if (now - lastLogTime >= LOG_INTERVAL_MS) {
            log.info(
                    "[PROGRESS] step={}, read={}, write={}",
                    stepExecution.getStepName(),
                    stepExecution.getReadCount(),
                    stepExecution.getWriteCount());

            executionContext.putLong(LAST_LOG_TIME_KEY, now);
        }
    }
}
