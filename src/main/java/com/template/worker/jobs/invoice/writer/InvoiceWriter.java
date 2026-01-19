package com.template.worker.jobs.invoice.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.template.worker.jobs.invoice.model.InvoiceEntity;

@Component
public class InvoiceWriter implements ItemWriter<InvoiceEntity> {
    @Override
    public void write(Chunk<? extends InvoiceEntity> chunk) throws Exception {}
}
