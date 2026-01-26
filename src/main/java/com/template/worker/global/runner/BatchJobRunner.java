package com.template.worker.global.runner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.template.worker.global.launcher.BatchJobLauncher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
                Optional.ofNullable(args.getOptionValues("spring.batch.job.name"))
                        .flatMap(values -> values.stream().findFirst())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Missing --spring.batch.job.name"));

        // invMonth가 없으면 현재 년월(yyyyMM)을 기본값으로 생성
        String invMonth =
                Optional.ofNullable(args.getOptionValues("invMonth"))
                        .flatMap(values -> values.stream().findFirst())
                        .orElseGet(
                                () ->
                                        LocalDate.now()
                                                .format(DateTimeFormatter.ofPattern("yyyyMM")));

        Job job = jobRegistry.getJob(jobName);

        JobParameters params =
                new JobParametersBuilder(jobExplorer)
                        .addString("invMonth", invMonth)
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters();

        log.info("▶ BATCH START job={} invMonth={}", jobName, invMonth);
        batchJobLauncher.launch(job, params);
    }
}
