package com.template.worker.jobs.invoice.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.jobs.invoice.model.InvoiceAggregationRow;
import com.template.worker.jobs.invoice.model.InvoiceEntity;

@Component
public class InvoiceProcessor implements ItemProcessor<InvoiceAggregationRow, InvoiceEntity> {

    @Override
    public InvoiceEntity process(InvoiceAggregationRow row) {

        return InvoiceEntity.builder()
                .subId(row.getSubId())
                .totalAmount(row.getTotalAmount())
                .totalDiscount(row.getTotalDiscount())
                .build();
    }
}
