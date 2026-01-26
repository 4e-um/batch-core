package com.template.worker.global.runner;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.template.worker.batch.orchestrator.UsageOrchestrator;
import com.template.worker.global.launcher.BatchJobLauncher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobRunner implements ApplicationRunner {

    private final BatchJobLauncher batchJobLauncher;
    private final UsageOrchestrator usageOrchestrator;
    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String jobName =
                args.getOptionValues("spring.batch.job.name").stream()
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Missing --spring.batch.job.name"));

        Job job = jobRegistry.getJob(jobName);

        JobParametersBuilder builder =
                new JobParametersBuilder(jobExplorer).addLong("run.id", System.currentTimeMillis());

        // 1️⃣ Orchestrator Job (파라미터 없음)
        if ("usageOrchestratorJob".equals(jobName)) {

            log.info("▶ BATCH START (orchestrator) job={}", jobName);
            usageOrchestrator.run();

        } else {
            String invMonth = args.getOptionValues("invMonth").get(0);

            builder.addString("invMonth", invMonth);

            log.info("▶ BATCH START (invMonth) job={} invMonth={}", jobName, invMonth);
            batchJobLauncher.launch(job, builder.toJobParameters());
        }
    }
}
