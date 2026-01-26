package com.template.worker.batch.usageaggregate.dto;

public record UsageMonthlyAggregation(Long subId, String period, long deltaBytes) {}
