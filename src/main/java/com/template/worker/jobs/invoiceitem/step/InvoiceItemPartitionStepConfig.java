package com.template.worker.jobs.invoiceitem.step;

import com.template.worker.jobs.invoiceitem.partition.InvoiceItemPartitionHandlerConfig;
import com.template.worker.jobs.invoiceitem.partition.SubscriptionRangePartitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemPartitionStepConfig {

  private final JobRepository jobRepository;
  private final SubscriptionRangePartitioner partitioner;
  private final InvoiceItemPartitionHandlerConfig partitionHandler;

  @Bean
  public Step invoiceItemPartitionStep() {
    return new StepBuilder("invoiceItemPartitionStep", jobRepository)
        .partitioner("invoiceItemWorkerStep", partitioner)
        .partitionHandler(partitionHandler.invoiceItemPartitionHandler())
        .build();
  }
}
