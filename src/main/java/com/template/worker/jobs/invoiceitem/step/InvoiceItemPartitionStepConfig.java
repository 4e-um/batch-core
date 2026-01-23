package com.template.worker.jobs.invoiceitem.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.template.worker.jobs.invoiceitem.partition.InvoiceItemPartitionHandlerConfig;
import com.template.worker.jobs.invoiceitem.partition.SubscriptionRangePartitioner;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemPartitionStepConfig {

    private final JobRepository jobRepository;
    private final SubscriptionRangePartitioner partitioner;
    private final InvoiceItemPartitionHandlerConfig partitionHandler;

    @Bean
    public Step planDiscountPartitionStep() {
        return buildPartitionStep(
                "planDiscountPartitionStep", "planDiscountWorkerStep", partitionHandler.planPartitionHandler());
    }

    @Bean
    public Step microPaymentPartitionStep() {
        return buildPartitionStep(
                "microPaymentPartitionStep", "microPaymentWorkerStep", partitionHandler.microPartitionHandler());
    }

    @Bean
    public Step vasPartitionStep() {
        return buildPartitionStep("vasPartitionStep", "vasWorkerStep", partitionHandler.vasPartitionHandler());
    }

    private Step buildPartitionStep(
            String stepName, String workerStepName, org.springframework.batch.core.partition.PartitionHandler handler) {
        return new StepBuilder(stepName, jobRepository)
                .partitioner(workerStepName, partitioner)
                .partitionHandler(handler)
                .build();
    }
}
