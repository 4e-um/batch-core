package com.template.worker.jobs.invoice.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceEntity {

    private Long subId;
    private String invMonth;

    private Long totalAmount;
    private Long totalDiscount;

    public Long getTotalPrice() {
        return totalAmount - totalDiscount;
    }
}