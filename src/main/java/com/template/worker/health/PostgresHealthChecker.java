package com.template.worker.health;

import java.sql.Connection;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostgresHealthChecker {

    private final DataSource dataSource;

    public HealthCheckResult check() {
        long startTime = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5);
            long responseTime = System.currentTimeMillis() - startTime;

            return HealthCheckResult.builder()
                    .serviceName("PostgreSQL")
                    .healthy(isValid)
                    .message(isValid ? "Connection successful" : "Connection invalid")
                    .checkedAt(LocalDateTime.now())
                    .responseTimeMs(responseTime)
                    .build();
        } catch (Exception e) {
            log.error("PostgreSQL health check failed", e);
            return HealthCheckResult.builder()
                    .serviceName("PostgreSQL")
                    .healthy(false)
                    .message("Connection failed: " + e.getMessage())
                    .checkedAt(LocalDateTime.now())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
}
