package com.template.worker.jobs.invoice.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.template.worker.global.config.JobParameterValidator;
import com.template.worker.global.listener.JobResultListener;
import com.template.worker.jobs.invoice.step.InvoicePartitionStepConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class InvoiceJobConfig {

    private final JobRepository jobRepository;
    private final JobParameterValidator jobParametersValidator;
    private final JobResultListener jobResultListener;
    private final InvoicePartitionStepConfig partitionStep;

    @Bean
    public Job invoiceJob() {
        return new JobBuilder("invoiceJob", jobRepository)
                .validator(jobParametersValidator)
                .listener(jobResultListener)
                .start(partitionStep.invoicePartitionStep())
                .build();
    }
}
