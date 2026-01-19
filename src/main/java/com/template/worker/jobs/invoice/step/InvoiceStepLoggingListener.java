package com.template.worker.jobs.invoice.step;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InvoiceStepLoggingListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("▶ Step 시작");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("▶ Read Count  = {}", stepExecution.getReadCount());
        log.info("▶ Write Count = {}", stepExecution.getWriteCount());
        return stepExecution.getExitStatus();
    }
}
