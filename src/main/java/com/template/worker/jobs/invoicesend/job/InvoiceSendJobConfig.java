package com.template.worker.jobs.invoicesend.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.template.worker.global.listener.JobResultListener;
import com.template.worker.jobs.invoicesend.model.InvoiceNotificationEvent;
import com.template.worker.jobs.invoicesend.model.InvoiceSendRecord;
import com.template.worker.jobs.invoicesend.processor.InvoiceSendProcessor;
import com.template.worker.jobs.invoicesend.writer.InvoiceSendWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InvoiceSendJobConfig {

    @Value("${spring.batch.jobs.invoice-send.chunk-size}")
    private int chunkSize;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final InvoiceSendProcessor invoiceSendProcessor;
    private final InvoiceSendWriter invoiceSendWriter;
    private final JobResultListener jobResultListener;

    @Bean
    public Job invoiceSendJob(Step invoiceSendStep) {
        return new JobBuilder("invoiceSendJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(invoiceSendStep)
                .build();
    }

    @Bean
    public Step invoiceSendStep(JdbcCursorItemReader<InvoiceSendRecord> invoiceJoinReader) {
        return new StepBuilder("invoiceSendStep", jobRepository)
                .<InvoiceSendRecord, InvoiceNotificationEvent>chunk(chunkSize, transactionManager)
                .reader(invoiceJoinReader)
                .processor(invoiceSendProcessor)
                .writer(invoiceSendWriter)
                .listener(jobResultListener)
                .build();
    }
}
