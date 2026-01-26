package com.template.worker.batch.orchestrator;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsageOrchestrator {

    private final JobLauncher jobLauncher;
    private final Job usageAggregationJob;
    private final Job usageNotificationJob;
    private final Job notificationSendJob;

    private final BatchTimeWindowService timeWindowService;

    public void run() throws Exception {
        LocalDateTime aggregationStart = LocalDateTime.now();
        LocalDateTime aggFrom = timeWindowService.resolve("usage-aggregation");

        JobParameters aggregationParams =
                new JobParametersBuilder()
                        .addLocalDateTime("fromTime", aggFrom)
                        .addLocalDateTime("toTime", aggregationStart)
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters();

        JobExecution aggregationExec = jobLauncher.run(usageAggregationJob, aggregationParams);

        if (aggregationExec.getStatus().isUnsuccessful()) {
            return;
        }
        timeWindowService.update("usage-aggregation", aggregationStart);

        LocalDateTime notificationStart = LocalDateTime.now();

        LocalDateTime notificationFrom = timeWindowService.resolve("usage-notification");

        JobParameters notificationParams =
                new JobParametersBuilder()
                        .addLocalDateTime("fromTime", notificationFrom)
                        .addLocalDateTime("toTime", notificationStart)
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters();

        JobExecution notificationExec = jobLauncher.run(usageNotificationJob, notificationParams);

        if (notificationExec.getStatus().isUnsuccessful()) {
            return;
        }

        timeWindowService.update("usage-notification", notificationStart);

        LocalDateTime sendStart = LocalDateTime.now();

        LocalDateTime sendFrom = timeWindowService.resolve("notification-send");

        JobParameters sendParams =
                new JobParametersBuilder()
                        .addLocalDateTime("fromTime", sendFrom)
                        .addLocalDateTime("toTime", sendStart)
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters();

        JobExecution sendExec = jobLauncher.run(notificationSendJob, sendParams);

        if (sendExec.getStatus().isUnsuccessful()) {
            return;
        }

        timeWindowService.update("notification-send", sendStart);
    }
}
