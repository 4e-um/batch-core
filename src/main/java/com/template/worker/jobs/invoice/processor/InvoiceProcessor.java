package com.template.worker.jobs.invoice.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.jobs.invoice.model.InvoiceEntity;
import com.template.worker.jobs.invoice.model.InvoiceItemRow;

@Component
public class InvoiceProcessor implements ItemProcessor<InvoiceItemRow, InvoiceEntity> {
    @Override
    public InvoiceEntity process(InvoiceItemRow item) throws Exception {
        return null;
    }
}
