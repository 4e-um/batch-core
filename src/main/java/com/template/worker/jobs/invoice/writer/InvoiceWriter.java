package com.template.worker.jobs.invoice.writer;

import com.template.worker.jobs.invoice.model.InvoiceEntity;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class InvoiceWriter implements ItemWriter<InvoiceEntity> {
  @Override
  public void write(Chunk<? extends InvoiceEntity> chunk) throws Exception {}
}
