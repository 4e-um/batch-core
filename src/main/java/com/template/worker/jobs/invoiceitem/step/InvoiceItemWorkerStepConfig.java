package com.template.worker.jobs.invoiceitem.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.template.worker.global.listener.PartitionTimingListener;
import com.template.worker.global.listener.TimeBasedChunkListener;
import com.template.worker.jobs.invoiceitem.model.InvoiceItemAggregateRow;
import com.template.worker.jobs.invoiceitem.model.InvoiceItemRecord;
import com.template.worker.jobs.invoiceitem.processor.InvoiceItemProcessor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemWorkerStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager primaryTxManager;
    private final ItemReader<InvoiceItemAggregateRow> planDiscountReader;
    private final ItemReader<InvoiceItemAggregateRow> microPaymentReader;
    private final ItemReader<InvoiceItemAggregateRow> vasReader;
    private final InvoiceItemProcessor processor;
    private final ItemWriter<InvoiceItemRecord> writer;
    private final PartitionTimingListener partitionTimingListener;
    private final TimeBasedChunkListener timeBasedChunkListener;

    @Value("${spring.batch.jobs.invoice-item.chunk-size}")
    int chunk;

    @Bean
    public Step planDiscountWorkerStep() {
        return buildWorkerStep("planDiscountWorkerStep", planDiscountReader);
    }

    @Bean
    public Step microPaymentWorkerStep() {
        return buildWorkerStep("microPaymentWorkerStep", microPaymentReader);
    }

    @Bean
    public Step vasWorkerStep() {
        return buildWorkerStep("vasWorkerStep", vasReader);
    }

    private Step buildWorkerStep(String stepName, ItemReader<InvoiceItemAggregateRow> reader) {
        return new StepBuilder(stepName, jobRepository)
                .<InvoiceItemAggregateRow, InvoiceItemRecord>chunk(chunk, primaryTxManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(partitionTimingListener)
                .listener(timeBasedChunkListener)
                .build();
    }
}
