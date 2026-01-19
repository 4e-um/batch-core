package com.template.worker.jobs.invoiceitem.partition;

import com.template.worker.jobs.invoiceitem.step.InvoiceItemWorkerStepConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemPartitionHandlerConfig {

    private final InvoiceItemWorkerStepConfig workerStep;

    @Value("${spring.batch.jobs.invoice-item.partition.grid-size}")
    private int gridSize;

    @Value("${spring.batch.jobs.invoice-item.partition.thread.core-pool-size}")
    private int corePoolSize;

    @Value("${spring.batch.jobs.invoice-item.partition.thread.max-pool-size}")
    private int maxPoolSize;

    @Value("${spring.batch.jobs.invoice-item.partition.thread.queue-capacity}")
    private int queueCapacity;

    @Bean
    public TaskExecutor invoiceItemPartitionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("invoice-item-partition-");
        executor.initialize();
        return executor;
    }

    @Bean
    public PartitionHandler invoiceItemPartitionHandler() {
        TaskExecutorPartitionHandler h = new TaskExecutorPartitionHandler();
        h.setStep(workerStep.invoiceItemWorkerStep());
        h.setTaskExecutor(invoiceItemPartitionExecutor());
        h.setGridSize(gridSize);
        return h;
    }
}