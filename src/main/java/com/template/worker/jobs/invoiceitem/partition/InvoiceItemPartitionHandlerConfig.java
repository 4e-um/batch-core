package com.template.worker.jobs.invoiceitem.partition;

import com.template.worker.jobs.invoiceitem.step.InvoiceItemWorkerStepConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemPartitionHandlerConfig {

  private final InvoiceItemWorkerStepConfig workerStep;
  private final TaskExecutor batchPartitionExecutor;

  @Value("${spring.batch.partition.invoiceitem}")
  private int gridSize;

  @Bean
  public PartitionHandler invoiceItemPartitionHandler() {
    TaskExecutorPartitionHandler h = new TaskExecutorPartitionHandler();
    h.setStep(workerStep.invoiceItemWorkerStep());
    h.setTaskExecutor(batchPartitionExecutor);
    h.setGridSize(gridSize);
    return h;
  }
}
