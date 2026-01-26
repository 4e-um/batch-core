package com.template.worker.batch.usagenotification.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.template.worker.batch.model.BatchStepMetricsListener;
import com.template.worker.batch.usagenotification.dto.UsageNotificationCandidate;
import com.template.worker.batch.usagenotification.dto.UsageNotificationSource;
import com.template.worker.batch.usagenotification.processor.UsageNotificationProcessor;
import com.template.worker.batch.usagenotification.writer.UsageNotificationWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UsageNotificationJobConfig {

    @Value("${spring.batch.jobs.usage.chunk-size}")
    private int chunkSize;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;

    @Qualifier("usageNotificationMonthlyReader")
    private final JdbcCursorItemReader<UsageNotificationSource> usageNotificationMonthlyReader;

    @Qualifier("usageNotificationDailyReader")
    private final JdbcCursorItemReader<UsageNotificationSource> usageNotificationDailyReader;

    private final UsageNotificationProcessor usageNotificationProcessor;
    private final UsageNotificationWriter usageNotificationWriter;
    private final BatchStepMetricsListener batchStepMetricsListener;

    @Bean
    public Job usageNotificationJob() {
        return new JobBuilder("usageNotificationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(usageNotificationMonthlyStep())
                .next(usageNotificationDailyStep())
                .build();
    }

    @Bean
    @JobScope
    public Step usageNotificationMonthlyStep() {
        return new StepBuilder("usageNotificationMonthlyStep", jobRepository)
                .<UsageNotificationSource, UsageNotificationCandidate>chunk(chunkSize, txManager)
                .reader(usageNotificationMonthlyReader)
                .processor(usageNotificationProcessor)
                .writer(usageNotificationWriter)
                .listener(batchStepMetricsListener)
                .build();
    }

    @Bean
    @JobScope
    public Step usageNotificationDailyStep() {
        return new StepBuilder("usageNotificationDailyStep", jobRepository)
                .<UsageNotificationSource, UsageNotificationCandidate>chunk(chunkSize, txManager)
                .reader(usageNotificationDailyReader)
                .processor(usageNotificationProcessor)
                .writer(usageNotificationWriter)
                .listener(batchStepMetricsListener)
                .build();
    }
}
