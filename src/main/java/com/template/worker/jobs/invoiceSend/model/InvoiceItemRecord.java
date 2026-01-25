package com.template.worker.jobs.invoiceSend.model;

public record InvoiceItemRecord(
        Long itemId,
        String invoiceName,
        String invoiceType,
        Integer value
) {
}
