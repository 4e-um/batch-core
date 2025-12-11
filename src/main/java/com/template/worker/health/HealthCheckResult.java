package com.template.worker.health;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HealthCheckResult {
    private final String serviceName;
    private final boolean healthy;
    private final String message;
    private final LocalDateTime checkedAt;
    private final long responseTimeMs;
}
