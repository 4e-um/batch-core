package com.template.worker.batch.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "batch_execution_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BatchExecutionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;
    private String stepName;

    private String status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private long durationMs;

    private long readCount;
    private long writeCount;
    private long filterCount;
    private long skipCount;
    private long commitCount;
    private long rollbackCount;

    private BigDecimal tps;

    @Column(columnDefinition = "jsonb")
    private String jobParameters;

    private LocalDateTime createdAt;
}
