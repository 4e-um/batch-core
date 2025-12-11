package com.template.worker.health;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoHealthChecker {

    private final MongoTemplate mongoTemplate;

    public HealthCheckResult check() {
        long startTime = System.currentTimeMillis();
        try {
            Document result = mongoTemplate.getDb().runCommand(new Document("ping", 1));

            boolean isOk = result.getDouble("ok") == 1.0;
            long responseTime = System.currentTimeMillis() - startTime;

            return HealthCheckResult.builder()
                    .serviceName("MongoDB")
                    .healthy(isOk)
                    .message(isOk ? "Ping successful" : "Ping failed")
                    .checkedAt(LocalDateTime.now())
                    .responseTimeMs(responseTime)
                    .build();
        } catch (Exception e) {
            log.error("MongoDB health check failed", e);
            return HealthCheckResult.builder()
                    .serviceName("MongoDB")
                    .healthy(false)
                    .message("Connection failed: " + e.getMessage())
                    .checkedAt(LocalDateTime.now())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
}
