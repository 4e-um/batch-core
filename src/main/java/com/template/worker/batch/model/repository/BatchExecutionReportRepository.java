package com.template.worker.batch.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.template.worker.batch.model.entity.BatchExecutionReport;

public interface BatchExecutionReportRepository extends JpaRepository<BatchExecutionReport, Long> {}
