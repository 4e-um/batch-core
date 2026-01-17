package com.template.worker.jobs.invoiceitem.step;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemEntity;
import com.template.worker.jobs.invoiceitem.model.InvoiceItemRaw;
import com.template.worker.jobs.invoiceitem.processor.InvoiceItemProcessor;
import com.template.worker.jobs.invoiceitem.reader.InvoiceItemReader;
import com.template.worker.jobs.invoiceitem.writer.InvoiceItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemWorkerStepConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager primaryTxManager;
  private final InvoiceItemReader reader;
  private final InvoiceItemProcessor processor;
  private final InvoiceItemWriter writer;

  @Value("${spring.batch.chunk.invoiceitem}")
  int chunk;

  @Bean
  public Step invoiceItemWorkerStep() {
    return new StepBuilder("invoiceItemWorkerStep", jobRepository)
        .<InvoiceItemRaw, InvoiceItemEntity>chunk(chunk, primaryTxManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }
}
