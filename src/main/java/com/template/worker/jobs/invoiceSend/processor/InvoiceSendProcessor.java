package com.template.worker.jobs.invoiceSend.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.jobs.invoiceSend.model.InvoiceAggregateRecord;
import com.template.worker.jobs.invoiceSend.model.InvoiceItemRecord;
import com.template.worker.jobs.invoiceSend.model.InvoiceSendRecord;

@Component
@StepScope
public class InvoiceSendProcessor
        implements ItemProcessor<InvoiceSendRecord, InvoiceAggregateRecord> {

    private Long currentInvId;
    private InvoiceSendRecord currentInvoice;
    private List<InvoiceItemRecord> currentItems;

    @Override
    public InvoiceAggregateRecord process(InvoiceSendRecord row) {

        if (currentInvId == null) {
            startNewInvoice(row);
            return null;
        }

        if (row.invId().equals(currentInvId)) {
            addItem(row);
            return null;
        }

        InvoiceAggregateRecord finished = buildAggregate();

        startNewInvoice(row);

        return finished;
    }

    private void startNewInvoice(InvoiceSendRecord row) {
        this.currentInvId = row.invId();
        this.currentInvoice = row;
        this.currentItems = new ArrayList<>(4);
        addItem(row);
    }

    private void addItem(InvoiceSendRecord row) {
        currentItems.add(
                new InvoiceItemRecord(
                        row.invItemId(),
                        row.itemName(),
                        row.itemType(),
                        row.itemValue().intValue()));
    }

    public InvoiceAggregateRecord flushLast() {
        if (currentInvId == null) return null;
        return buildAggregate();
    }

    private InvoiceAggregateRecord buildAggregate() {
        return new InvoiceAggregateRecord(
                currentInvoice.invId(),
                currentInvoice.invNo(),
                currentInvoice.subId(),
                currentInvoice.invMonth(),
                currentInvoice.phone_enc(),
                currentInvoice.email_enc(),
                currentInvoice.name(),
                currentInvoice.totalPrice(),
                currentInvoice.createdAt(),
                currentInvoice.dueDate(),
                List.copyOf(currentItems));
    }
}
