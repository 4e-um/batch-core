package com.template.worker.batch.usageaggregate.dto;

public record UsageDailyAggregation(Long subId, String usageDate, long deltaBytes) {}
