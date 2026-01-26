package com.template.worker.global.config;

import jakarta.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.push.PushMeterRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MetricsShutdownConfig {

    private final MeterRegistry meterRegistry;

    @PreDestroy
    public void onShutdown() {
        log.info("[METRICS] Flushing metrics before shutdown...");

        if (meterRegistry instanceof CompositeMeterRegistry composite) {
            composite.getRegistries().forEach(this::closeRegistry);
        } else {
            closeRegistry(meterRegistry);
        }

        log.info("[METRICS] Metrics flush completed");
    }

    private void closeRegistry(MeterRegistry registry) {
        if (registry instanceof PushMeterRegistry pushRegistry) {
            log.info(
                    "[METRICS] Closing registry (triggers final publish): {}",
                    registry.getClass().getSimpleName());
            pushRegistry.close();
        }
    }
}
