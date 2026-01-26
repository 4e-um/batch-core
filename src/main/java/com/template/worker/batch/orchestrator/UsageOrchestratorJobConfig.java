package com.template.worker.batch.orchestrator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UsageOrchestratorJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UsageOrchestrator usageOrchestrator;

    @Bean
    public Job usageOrchestratorJob() {
        return new JobBuilder("usageOrchestratorJob", jobRepository)
                .start(usageOrchestratorStep())
                .build();
    }

    @Bean
    public Step usageOrchestratorStep() {
        return new StepBuilder("usageOrchestratorStep", jobRepository)
                .tasklet(
                        (contribution, chunkContext) -> {
                            usageOrchestrator.run();
                            return RepeatStatus.FINISHED;
                        },
                        transactionManager)
                .build();
    }
}
