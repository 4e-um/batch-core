package com.template.worker.global.config;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchMetricsConfig {
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void init() {
        log.info(
                "[METRICS] BatchMetricsConfig initialized with registry: {}",
                meterRegistry.getClass().getSimpleName());
    }

    // Test gauge - always visible with value 1 (for debugging OTLP export)
    @Bean
    public AtomicInteger batchMetricsTestGauge() {
        AtomicInteger value = new AtomicInteger(1);
        Gauge.builder("spring.batch.metrics.test", value, AtomicInteger::get)
                .description("Test gauge to verify OTLP metrics export")
                .register(meterRegistry);
        log.info("[METRICS] Registered test gauge: spring.batch.metrics.test = 1");
        return value;
    }

    // Active jobs gauge
    @Bean
    public AtomicInteger activeJobsGauge() {
        AtomicInteger value = new AtomicInteger(0);
        Gauge.builder("spring.batch.job.active", value, AtomicInteger::get)
                .description("Currently active batch jobs")
                .register(meterRegistry);
        log.info("[METRICS] Registered gauge: spring.batch.job.active");
        return value;
    }

    // Completed/Failed job counters
    @Bean
    public Counter jobCompletedCounter() {
        Counter counter =
                Counter.builder("spring.batch.job.completed.total")
                        .description("Total completed batch jobs")
                        .register(meterRegistry);
        log.info("[METRICS] Registered counter: spring.batch.job.completed.total");
        return counter;
    }

    @Bean
    public Counter jobFailedCounter() {
        Counter counter =
                Counter.builder("spring.batch.job.failed.total")
                        .description("Total failed batch jobs")
                        .register(meterRegistry);
        log.info("[METRICS] Registered counter: spring.batch.job.failed.total");
        return counter;
    }

    // Partition count by status
    @Bean
    public Counter partitionCompletedCounter() {
        Counter counter =
                Counter.builder("spring.batch.partition.count")
                        .tag("status", "COMPLETED")
                        .description("Total completed partitions")
                        .register(meterRegistry);
        log.info("[METRICS] Registered counter: spring.batch.partition.count (COMPLETED)");
        return counter;
    }

    @Bean
    public Counter partitionFailedCounter() {
        Counter counter =
                Counter.builder("spring.batch.partition.count")
                        .tag("status", "FAILED")
                        .description("Total failed partitions")
                        .register(meterRegistry);
        log.info("[METRICS] Registered counter: spring.batch.partition.count (FAILED)");
        return counter;
    }

    // Chunk count
    @Bean
    public Counter chunkCounter() {
        Counter counter =
                Counter.builder("spring.batch.chunk.count")
                        .description("Total chunks processed")
                        .register(meterRegistry);
        log.info("[METRICS] Registered counter: spring.batch.chunk.count");
        return counter;
    }

    // Step item count
    @Bean
    public Counter stepItemCounter() {
        Counter counter =
                Counter.builder("spring.batch.step.item.count")
                        .description("Total items processed by steps")
                        .register(meterRegistry);
        log.info("[METRICS] Registered counter: spring.batch.step.item.count");
        return counter;
    }

    // Step duration histogram
    @Bean
    public Timer stepDurationTimer() {
        Timer timer =
                Timer.builder("spring.batch.step.duration.seconds")
                        .publishPercentileHistogram()
                        .description("Step duration histogram")
                        .register(meterRegistry);
        log.info("[METRICS] Registered timer: spring.batch.step.duration.seconds");
        return timer;
    }
}
