package com.template.worker.jobs.invoice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItemRow {

    private Long subId;
    private String billingYm;
    private Long amount;   // +/-
}
