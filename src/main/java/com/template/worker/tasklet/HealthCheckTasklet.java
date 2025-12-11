package com.template.worker.tasklet;

import com.template.worker.health.HealthCheckResult;
import com.template.worker.health.MongoHealthChecker;
import com.template.worker.health.PostgresHealthChecker;
import com.template.worker.slack.SlackNotificationService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckTasklet implements Tasklet {

    private final PostgresHealthChecker postgresHealthChecker;
    private final MongoHealthChecker mongoHealthChecker;
    private final SlackNotificationService slackNotificationService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("Starting health check tasklet");

        List<HealthCheckResult> results = new ArrayList<>();

        log.info("Checking PostgreSQL health...");
        HealthCheckResult postgresResult = postgresHealthChecker.check();
        results.add(postgresResult);
        log.info("PostgreSQL health: {}", postgresResult.isHealthy() ? "OK" : "FAIL");

        log.info("Checking MongoDB health...");
        HealthCheckResult mongoResult = mongoHealthChecker.check();
        results.add(mongoResult);
        log.info("MongoDB health: {}", mongoResult.isHealthy() ? "OK" : "FAIL");

        log.info("Sending results to Slack...");
        slackNotificationService.sendHealthCheckResults(results);

        log.info("Health check tasklet completed");
        return RepeatStatus.FINISHED;
    }
}
