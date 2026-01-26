package com.template.worker.batch.notificationsend.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.template.worker.batch.notificationsend.dto.NotificationMessage;
import com.template.worker.batch.notificationsend.processor.NotificationSendProcessor;
import com.template.worker.batch.notificationsend.writer.NotificationSendWriter;
import com.template.worker.batch.usagenotification.dto.UsageNotificationOutboxRow;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class NotificationSendJobConfig {

    @Value("${spring.batch.jobs.usage.chunk-size}")
    private int chunkSize;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;

    private final JdbcCursorItemReader<UsageNotificationOutboxRow> outboxReader;
    private final NotificationSendProcessor notificationSendProcessor;
    private final NotificationSendWriter notificationSendWriter;

    @Bean
    public Job notificationSendJob() {
        return new JobBuilder("notificationSendJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(notificationSendStep())
                .build();
    }

    @JobScope
    @Bean
    public Step notificationSendStep() {
        return new StepBuilder("notificationSendStep", jobRepository)
                .<UsageNotificationOutboxRow, NotificationMessage>chunk(chunkSize, txManager)
                .reader(outboxReader)
                .processor(notificationSendProcessor)
                .writer(notificationSendWriter)
                .build();
    }
}
