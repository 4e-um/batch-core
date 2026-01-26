package com.template.worker.global.config;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchMetricsConfig {
    private final MeterRegistry meterRegistry;

    // Active jobs gauge
    @Bean
    public AtomicInteger activeJobsGauge() {
        return meterRegistry.gauge("spring.batch.job.active", new AtomicInteger(0));
    }

    // Completed/Failed job counters
    @Bean
    public Counter jobCompletedCounter() {
        return Counter.builder("spring.batch.job.completed.total")
                .description("Total completed batch jobs")
                .register(meterRegistry);
    }

    @Bean
    public Counter jobFailedCounter() {
        return Counter.builder("spring.batch.job.failed.total")
                .description("Total failed batch jobs")
                .register(meterRegistry);
    }

    // Partition count by status
    @Bean
    public Counter partitionCompletedCounter() {
        return Counter.builder("spring.batch.partition.count")
                .tag("status", "COMPLETED")
                .description("Total completed partitions")
                .register(meterRegistry);
    }

    @Bean
    public Counter partitionFailedCounter() {
        return Counter.builder("spring.batch.partition.count")
                .tag("status", "FAILED")
                .description("Total failed partitions")
                .register(meterRegistry);
    }

    // Chunk count
    @Bean
    public Counter chunkCounter() {
        return Counter.builder("spring.batch.chunk.count")
                .description("Total chunks processed")
                .register(meterRegistry);
    }

    // Step item count
    @Bean
    public Counter stepItemCounter() {
        return Counter.builder("spring.batch.step.item.count")
                .description("Total items processed by steps")
                .register(meterRegistry);
    }

    // Step duration histogram
    @Bean
    public Timer stepDurationTimer() {
        return Timer.builder("spring.batch.step.duration.seconds")
                .publishPercentileHistogram()
                .description("Step duration histogram")
                .register(meterRegistry);
    }
}
