package com.template.worker.jobs.invoiceitem.writer;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemEntity;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class InvoiceItemWriter implements ItemWriter<InvoiceItemEntity> {
    @Override
    public void write(Chunk<? extends InvoiceItemEntity> chunk) throws Exception {}
}