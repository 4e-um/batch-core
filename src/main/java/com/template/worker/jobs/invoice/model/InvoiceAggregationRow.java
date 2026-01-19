package com.template.worker.jobs.invoice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceAggregationRow {

    private Long subId;
    private Long totalAmount;
    private Long totalDiscount;
}
