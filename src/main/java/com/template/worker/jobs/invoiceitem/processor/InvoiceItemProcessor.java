package com.template.worker.jobs.invoiceitem.processor;

import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemAggregateRow;
import com.template.worker.jobs.invoiceitem.model.InvoiceItemRecord;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoiceItemProcessor
        implements ItemProcessor<InvoiceItemAggregateRow, InvoiceItemRecord> {

    @Override
    public InvoiceItemRecord process(InvoiceItemAggregateRow row) {

        return InvoiceItemRecord.builder()
                .subId(row.subId())
                .invMonth(row.invMonth())
                .type(row.type())
                .valueType(row.valueType())
                .name(row.name())
                .value(row.value())
                .targetScope(row.targetScope())
                .sourceId(row.sourceId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
