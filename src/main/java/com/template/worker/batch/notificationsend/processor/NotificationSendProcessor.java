package com.template.worker.batch.notificationsend.processor;

import java.util.Map;
import java.util.UUID;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.batch.notificationsend.dto.NotificationMessage;
import com.template.worker.batch.usagenotification.dto.UsageNotificationOutboxRow;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationSendProcessor
        implements ItemProcessor<UsageNotificationOutboxRow, NotificationMessage> {

    @Override
    public NotificationMessage process(UsageNotificationOutboxRow item) {

        return new NotificationMessage(
                UUID.randomUUID(),
                item.id(),
                101L,
                Map.of(
                        "subId", item.subId(),
                        "phoneNumber", item.phoneNumber(),
                        "email", item.email()),
                Map.of(
                        "period", item.period(),
                        "threshold", item.threshold(),
                        "percent", item.percent(),
                        "totalUsedMb", item.totalUsedMb(),
                        "allotmentMb", item.allotmentMb(),
                        "phoneNumber", item.phoneNumber(),
                        "email", item.email(),
                        "createdAt", item.createdAt()));
    }
}
