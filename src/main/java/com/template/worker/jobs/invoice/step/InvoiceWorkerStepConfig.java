package com.template.worker.jobs.invoice.step;

import com.template.worker.jobs.invoice.model.InvoiceEntity;
import com.template.worker.jobs.invoice.model.InvoiceItemRow;
import com.template.worker.jobs.invoice.processor.InvoiceProcessor;
import com.template.worker.jobs.invoice.reader.InvoiceReader;
import com.template.worker.jobs.invoice.writer.InvoiceWriter;
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
public class InvoiceWorkerStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager primaryTxManager;
    private final InvoiceReader reader;
    private final InvoiceProcessor processor;
    private final InvoiceWriter writer;

    @Value("${spring.batch.chunk.invoice}")
    int chunk;

    @Bean
    public Step invoiceWorkerStep() {
        return new StepBuilder("invoiceWorkerStep", jobRepository)
                .<InvoiceItemRow, InvoiceEntity>chunk(chunk, primaryTxManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}