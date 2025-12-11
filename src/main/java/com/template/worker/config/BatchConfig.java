package com.template.worker.config;

import com.template.worker.listener.JobCompletionListener;
import com.template.worker.tasklet.HealthCheckTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final HealthCheckTasklet healthCheckTasklet;
    private final JobCompletionListener jobCompletionListener;

    @Bean
    public Job healthCheckJob(JobRepository jobRepository, Step healthCheckStep) {
        return new JobBuilder("healthCheckJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .start(healthCheckStep)
                .build();
    }

    @Bean
    public Step healthCheckStep(
            JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("healthCheckStep", jobRepository)
                .tasklet(healthCheckTasklet, transactionManager)
                .build();
    }
}
