package com.template.worker.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BatchTaskExecutorConfig {

    @Bean
    public ThreadPoolTaskExecutor batchPartitionExecutor(
            @Value("${spring.batch.thread.core-pool-size}") int core,
            @Value("${spring.batch.thread.max-pool-size}") int max,
            @Value("${spring.batch.thread.queue-capacity}") int queue) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setThreadNamePrefix("batch-partition-");
        executor.initialize();
        return executor;
    }
}