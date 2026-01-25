package com.template.worker.jobs.invoicesend.model;

public record InvoiceItemRecord(
        Long itemId, String invoiceName, String invoiceType, Integer value) {}
