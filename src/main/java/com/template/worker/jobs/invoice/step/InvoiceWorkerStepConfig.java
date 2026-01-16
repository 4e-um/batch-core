package com.template.worker.jobs.invoice.step;

import com.template.worker.jobs.invoice.model.InvoiceAggregationRow;
import com.template.worker.jobs.invoice.model.InvoiceEntity;
import com.template.worker.jobs.invoice.processor.InvoiceProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

@Configuration
@RequiredArgsConstructor
public class InvoiceWorkerStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ItemReader<InvoiceAggregationRow> invoicePagingReader;
    private final InvoiceProcessor processor;
    private final ItemWriter<InvoiceEntity> invoiceWriter;
    private final InvoiceStepLoggingListener stepLoggingListener;

    @Bean
    public Step invoiceWorkerStep() {

        return new StepBuilder("invoiceWorkerStep", jobRepository)
                .<InvoiceAggregationRow, InvoiceEntity>chunk(1000, transactionManager)
                .reader(invoicePagingReader)   // ✅ StepScope Bean 주입
                .processor(processor)
                .writer(invoiceWriter)         // ✅ 정상 writer
                .listener(stepLoggingListener)
                .build();
    }
}