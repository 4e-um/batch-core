package com.template.worker.batch.usagenotification.dto;

public record UsageNotificationCandidate(
        Long subId,
        String period,
        String unit,
        String planName,
        int threshold,
        double percent,
        long totalUsedMb,
        long allotmentMb) {}
