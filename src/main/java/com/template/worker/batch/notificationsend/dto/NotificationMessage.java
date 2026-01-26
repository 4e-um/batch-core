package com.template.worker.batch.notificationsend.dto;

import java.util.Map;
import java.util.UUID;

public record NotificationMessage(
        UUID eventId,
        Long id,
        Long templateGroupId,
        Map<String, Object> subscriptionInfo,
        Map<String, Object> variables) {}
