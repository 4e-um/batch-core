package com.template.worker.jobs.invoiceSend.model;

import java.time.LocalDateTime;

public record InvoiceSendRecord(

        Long invId,
        Long invNo,
        Long subId,
        String invMonth,
        String phone_enc,
        String email_enc,
        String name,
        Integer totalPrice,
        LocalDateTime createdAt,
        LocalDateTime dueDate,

        Long invItemId,
        String itemName,
        String itemType,
        Double itemValue
) {
}
