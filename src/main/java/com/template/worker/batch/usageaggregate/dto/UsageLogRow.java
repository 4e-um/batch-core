package com.template.worker.batch.usageaggregate.dto;

import java.time.LocalDateTime;

public record UsageLogRow(Long subId, Long usedBytes, LocalDateTime eventTime) {}
