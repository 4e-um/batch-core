package com.template.worker.batch.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.worker.batch.model.entity.BatchExecutionReport;
import com.template.worker.batch.model.repository.BatchExecutionReportRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchStepMetricsListener implements StepExecutionListener {
    private final BatchExecutionReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        JobExecution jobExecution = stepExecution.getJobExecution();

        LocalDateTime start =
                Objects.requireNonNull(stepExecution.getStartTime())
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime();

        LocalDateTime end =
                Objects.requireNonNull(stepExecution.getEndTime())
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime();

        long durationMs = Duration.between(start, end).toMillis();

        long readCount = stepExecution.getReadCount();
        BigDecimal tps =
                durationMs > 0
                        ? BigDecimal.valueOf(readCount)
                                .multiply(BigDecimal.valueOf(1000))
                                .divide(BigDecimal.valueOf(durationMs), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

        String paramsJson;
        try {
            paramsJson =
                    objectMapper.writeValueAsString(
                            jobExecution.getJobParameters().getParameters());
        } catch (Exception e) {
            paramsJson = "{}";
        }

        reportRepository.save(
                BatchExecutionReport.builder()
                        .jobName(jobExecution.getJobInstance().getJobName())
                        .stepName(stepExecution.getStepName())
                        .status(stepExecution.getStatus().toString())
                        .startedAt(start)
                        .endedAt(end)
                        .durationMs(durationMs)
                        .readCount(readCount)
                        .writeCount(stepExecution.getWriteCount())
                        .filterCount(stepExecution.getFilterCount())
                        .skipCount(stepExecution.getSkipCount())
                        .commitCount(stepExecution.getCommitCount())
                        .rollbackCount(stepExecution.getRollbackCount())
                        .tps(tps)
                        .jobParameters(paramsJson)
                        .createdAt(LocalDateTime.now())
                        .build());

        return stepExecution.getExitStatus();
    }
}
