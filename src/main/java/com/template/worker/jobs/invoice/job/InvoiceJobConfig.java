package com.template.worker.jobs.invoice.job;

import com.template.worker.global.config.JobParameterValidator;
import com.template.worker.global.listener.JobResultListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InvoiceJobConfig {

    private final JobParameterValidator jobParameterValidator;
    private final JobRepository jobRepository;
    private final Step invoicePartitionStep;
    private final JobResultListener jobResultListener;

    @Bean
    public Job invoiceJob() {
        return new JobBuilder("invoiceJob", jobRepository)
                .validator(jobParameterValidator)
                .start(invoicePartitionStep)
                .listener(jobResultListener)
                .build();
    }
}