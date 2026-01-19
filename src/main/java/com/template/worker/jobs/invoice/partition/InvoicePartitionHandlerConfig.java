package com.template.worker.jobs.invoice.partition;

import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.template.worker.jobs.invoice.step.InvoiceWorkerStepConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InvoicePartitionHandlerConfig {

    private final InvoiceWorkerStepConfig workerStep;

    @Value("${spring.batch.jobs.invoice.partition.grid-size}")
    private int gridSize;

    @Value("${spring.batch.jobs.invoice.partition.thread.core-pool-size}")
    private int corePoolSize;

    @Value("${spring.batch.jobs.invoice.partition.thread.max-pool-size}")
    private int maxPoolSize;

    @Value("${spring.batch.jobs.invoice.partition.thread.queue-capacity}")
    private int queueCapacity;

    @Bean
    public TaskExecutor invoicePartitionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("invoice-partition-");
        executor.initialize();
        return executor;
    }

    @Bean
    public PartitionHandler invoicePartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(workerStep.invoiceWorkerStep());
        handler.setTaskExecutor(invoicePartitionExecutor());
        handler.setGridSize(gridSize);
        return handler;
    }
}
