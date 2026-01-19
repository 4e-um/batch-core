package com.template.worker.jobs.invoiceitem.model;

public record InvoiceItemAggregateRow(
    Long subId,
    String invMonth,
    String type,
    String valueType,
    String name,
    Double value,
    String targetScope,
    Long sourceId) {}
