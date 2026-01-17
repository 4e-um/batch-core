package com.template.worker.jobs.invoice.step;

import com.template.worker.jobs.invoice.partition.CustomerRangePartitioner;
import com.template.worker.jobs.invoice.partition.InvoicePartitionHandlerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InvoicePartitionStepConfig {

  private final JobRepository jobRepository;
  private final CustomerRangePartitioner partitioner;
  private final InvoicePartitionHandlerConfig partitionHandler;

  @Bean
  public Step invoicePartitionStep() {
    return new StepBuilder("invoicePartitionStep", jobRepository)
        .partitioner("invoiceWorkerStep", partitioner)
        .partitionHandler(partitionHandler.invoicePartitionHandler())
        .build();
  }
}
