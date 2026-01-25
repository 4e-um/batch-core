package com.template.worker.jobs.invoicesend.model;

import java.time.LocalDateTime;
import java.util.List;

public record InvoiceAggregateRecord(
        Long invId,
        Long invNo,
        Long subId,
        String invMonth,
        String phoneEnc,
        String emailEnc,
        String name,
        Integer totalPrice,
        LocalDateTime createdAt,
        LocalDateTime dueDate,
        List<InvoiceItemRecord> items) {}
