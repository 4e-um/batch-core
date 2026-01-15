package com.template.worker.global.runner;

import com.template.worker.global.launcher.BatchJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobRunner implements ApplicationRunner {

    private final BatchJobLauncher batchJobLauncher;
    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String jobName =
                args.getOptionValues("spring.batch.job.name").stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Missing --spring.batch.job.name"));

        String billingYm =
                args.getOptionValues("billingYm").stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Missing billingYm=yyyyMM"));

        Job job = jobRegistry.getJob(jobName);

        JobParameters params =
                new JobParametersBuilder(jobExplorer)
                        .addString("billingYm", billingYm)
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters();

        log.info("▶ BATCH START job={} billingYm={}", jobName, billingYm);
        batchJobLauncher.launch(job, params);
    }
}