package com.template.worker.batch.usageaggregate.config;

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
import com.template.worker.batch.usageaggregate.dto.UsageDailyAggregation;
import com.template.worker.batch.usageaggregate.dto.UsageLogRow;
import com.template.worker.batch.usageaggregate.dto.UsageMonthlyAggregation;
import com.template.worker.batch.usageaggregate.processor.UsageDailyProcessor;
import com.template.worker.batch.usageaggregate.processor.UsageMonthlyProcessor;
import com.template.worker.batch.usageaggregate.writer.UsageSummaryDailyWriter;
import com.template.worker.batch.usageaggregate.writer.UsageSummaryMonthlyWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UsageAggregationJobConfig {

    @Value("${spring.batch.jobs.usage.chunk-size}")
    private int chunkSize;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;

    @Qualifier("usageLogDailyReader")
    private final JdbcCursorItemReader<UsageLogRow> usageLogDailyReader;

    @Qualifier("usageLogMonthlyReader")
    private final JdbcCursorItemReader<UsageLogRow> usageLogMonthlyReader;

    private final UsageDailyProcessor usageDailyProcessor;
    private final UsageMonthlyProcessor usageMonthlyProcessor;

    private final UsageSummaryDailyWriter usageSummaryDailyWriter;
    private final UsageSummaryMonthlyWriter usageSummaryMonthlyWriter;

    private final BatchStepMetricsListener batchStepMetricsListener;

    @Bean
    public Job usageAggregationJob() {
        return new JobBuilder("usageAggregationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dailyAggregationStep())
                .next(monthlyAggregationStep())
                .build();
    }

    @JobScope
    @Bean
    public Step dailyAggregationStep() {
        return new StepBuilder("dailyAggregationStep", jobRepository)
                .<UsageLogRow, UsageDailyAggregation>chunk(chunkSize, txManager)
                .reader(usageLogDailyReader)
                .processor(usageDailyProcessor)
                .writer(usageSummaryDailyWriter)
                .listener(batchStepMetricsListener)
                .build();
    }

    @JobScope
    @Bean
    public Step monthlyAggregationStep() {
        return new StepBuilder("monthlyAggregationStep", jobRepository)
                .<UsageLogRow, UsageMonthlyAggregation>chunk(chunkSize, txManager)
                .reader(usageLogMonthlyReader)
                .processor(usageMonthlyProcessor)
                .writer(usageSummaryMonthlyWriter)
                .listener(batchStepMetricsListener)
                .build();
    }
}
