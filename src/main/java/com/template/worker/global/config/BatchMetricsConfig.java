package com.template.worker.global.config;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

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

    // Note: Counter and Timer metrics are now created dynamically with job_name tag
    // in the listener classes (JobResultListener, PartitionTimingListener, TimeBasedChunkListener)
    // Metrics:
    // - spring.batch.job.completed.total (job_name)
    // - spring.batch.job.failed.total (job_name)
    // - spring.batch.partition.count (status, job_name)
    // - spring.batch.chunk.count (job_name)
    // - spring.batch.step.item.count (job_name)
    // - spring.batch.step.duration (job_name)
}
