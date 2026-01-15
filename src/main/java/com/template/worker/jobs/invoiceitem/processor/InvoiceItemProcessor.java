package com.template.worker.jobs.invoiceitem.processor;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemEntity;
import com.template.worker.jobs.invoiceitem.model.InvoiceItemRaw;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class InvoiceItemProcessor implements ItemProcessor<InvoiceItemRaw, InvoiceItemEntity> {
    @Override
    public InvoiceItemEntity process(InvoiceItemRaw item) throws Exception {
        return null;
    }
}