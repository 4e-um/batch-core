package com.template.worker.jobs.invoiceitem.job;

import com.template.worker.global.config.JobParameterValidator;
import com.template.worker.global.listener.JobResultListener;
import com.template.worker.jobs.invoiceitem.step.InvoiceItemPartitionStepConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class InvoiceItemJobConfig {

  private final JobRepository jobRepository;
  private final JobParameterValidator jobParameterValidator;
  private final JobResultListener jobResultListener;
  private final InvoiceItemPartitionStepConfig partitionStep;

  @Bean
  public Job invoiceItemJob() {
    return new JobBuilder("invoiceItemJob", jobRepository)
        .validator(jobParameterValidator)
        .listener(jobResultListener)
        .start(partitionStep.invoiceItemPartitionStep())
        .build();
  }
}
