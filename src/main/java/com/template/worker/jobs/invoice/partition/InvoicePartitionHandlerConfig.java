package com.template.worker.jobs.invoice.partition;

import com.template.worker.jobs.invoice.step.InvoiceWorkerStepConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@RequiredArgsConstructor
public class InvoicePartitionHandlerConfig {

  private final InvoiceWorkerStepConfig workerStep;
  private final TaskExecutor batchPartitionExecutor;

  @Value("${spring.batch.partition.invoice}")
  private int gridSize;

  @Bean
  public PartitionHandler invoicePartitionHandler() {
    TaskExecutorPartitionHandler h = new TaskExecutorPartitionHandler();
    h.setStep(workerStep.invoiceWorkerStep());
    h.setTaskExecutor(batchPartitionExecutor);
    h.setGridSize(gridSize);
    return h;
  }
}
