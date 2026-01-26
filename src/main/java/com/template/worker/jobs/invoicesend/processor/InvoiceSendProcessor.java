package com.template.worker.jobs.invoicesend.processor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.jobs.invoicesend.model.InvoiceItemRecord;
import com.template.worker.jobs.invoicesend.model.InvoiceNotificationEvent;
import com.template.worker.jobs.invoicesend.model.InvoiceSendRecord;

@Component
@StepScope
public class InvoiceSendProcessor
        implements ItemProcessor<InvoiceSendRecord, InvoiceNotificationEvent> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Long currentInvId;
    private InvoiceSendRecord currentInvoice;
    private List<InvoiceItemRecord> currentItems;

    @Override
    public InvoiceNotificationEvent process(InvoiceSendRecord row) {

        if (currentInvId == null) {
            start(row);
            return null;
        }

        if (row.invId().equals(currentInvId)) {
            addItem(row);
            return null;
        }

        InvoiceNotificationEvent finished = buildEvent();
        start(row);
        return finished;
    }

    private void start(InvoiceSendRecord row) {
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

    public InvoiceNotificationEvent flushLast() {
        if (currentInvId == null) {
            return null;
        }
        return buildEvent();
    }

    private InvoiceNotificationEvent buildEvent() {

        String planName =
                currentItems.stream()
                        .filter(i -> "PLAN".equals(i.invoiceType()))
                        .map(InvoiceItemRecord::invoiceName)
                        .findFirst()
                        .orElse(null);

        List<InvoiceNotificationEvent.Item> items =
                currentItems.stream()
                        .map(
                                i ->
                                        new InvoiceNotificationEvent.Item(
                                                mapItemName(i.invoiceType()),
                                                i.invoiceName(),
                                                i.value()))
                        .toList();

        return new InvoiceNotificationEvent(
                UUID.randomUUID(),
                1L, // templateGroupId 고정
                new InvoiceNotificationEvent.SubscriptionInfo(
                        currentInvoice.subId(),
                        currentInvoice.phone_enc(),
                        currentInvoice.email_enc()),
                new InvoiceNotificationEvent.Variables(
                        currentInvoice.invNo().toString(),
                        currentInvoice.name(),
                        currentInvoice.phone_enc(),
                        planName,
                        currentInvoice.createdAt().format(DATE_FORMAT),
                        currentInvoice.dueDate().format(DATE_FORMAT),
                        currentInvoice.totalPrice(),
                        "PAID",
                        items));
    }

    private String mapItemName(String type) {
        return switch (type) {
            case "PLAN" -> "기본 요금";
            case "VAS" -> "부가서비스";
            case "DISCOUNT" -> "할인";
            case "MICRO" -> "소액 결제";
            default -> "기타";
        };
    }
}
