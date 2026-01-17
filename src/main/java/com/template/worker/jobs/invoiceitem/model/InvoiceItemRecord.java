package com.template.worker.jobs.invoiceitem.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InvoiceItemRecord {
    private Long subId;
    private String invMonth;
    private String name;
    private String type;
    private String valueType;
    private Double value;
    private String targetScope;
    private LocalDateTime createdAt;
}
