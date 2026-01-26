package com.template.worker.batch.usagenotification.dto;

public record UsageNotificationSource(
        Long subId,
        String period,
        String unit,
        String planName,
        long totalUsedBytes,
        long allotmentAmount) {}
