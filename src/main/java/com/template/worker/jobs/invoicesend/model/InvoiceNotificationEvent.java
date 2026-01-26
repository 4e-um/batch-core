package com.template.worker.jobs.invoicesend.model;

import java.util.List;
import java.util.UUID;

public record InvoiceNotificationEvent(
        UUID eventId,
        Long templateGroupId,
        SubscriptionInfo subscriptionInfo,
        Variables variables) {

    public record SubscriptionInfo(Long subId, String phoneNumber, String email) {}

    public record Variables(
            String invoiceNumber,
            String customerName,
            String phoneNumber,
            String planName,
            String billingDate,
            String dueDate,
            Integer totalAmount,
            String paymentStatus,
            List<Item> items) {}

    public record Item(String name, String detail, Integer amount) {}
}
