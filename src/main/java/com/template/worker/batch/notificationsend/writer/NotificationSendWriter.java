package com.template.worker.batch.notificationsend.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.worker.batch.model.repository.UsageNotificationOutboxRepository;
import com.template.worker.batch.notificationsend.dto.NotificationMessage;
import com.template.worker.batch.notificationsend.dto.NotificationSendTask;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationSendWriter implements ItemWriter<NotificationMessage> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UsageNotificationOutboxRepository repository;
    private final ObjectMapper objectMapper;
    private static final int MAX_FAILURE_REASON_LENGTH = 255;

    @Override
    public void write(Chunk<? extends NotificationMessage> items) {

        // usageNotificationOutboxId 추출
        List<Long> ids = items.getItems().stream().map(NotificationMessage::id).toList();

        // PROCESSING Status 먼저 DB 반영
        repository.markProcessing(ids);

        // Kafka 비동기 발행
        List<NotificationSendTask> tasks = new ArrayList<>();

        Map<Long, String> failedReasons = new HashMap<>();

        for (NotificationMessage event : items) {
            try {
                String payload = objectMapper.writeValueAsString(event);
                String key = String.valueOf(event.subscriptionInfo().get("subId"));

                CompletableFuture<SendResult<String, String>> future =
                        kafkaTemplate.send("usage", key, payload);

                tasks.add(new NotificationSendTask(event, future));
            } catch (Exception e) {
                failedReasons.put(event.id(), safeFailureReason(e));
            }
        }

        // Chunk 단위 ACK 동기화
        List<Long> successIds = new ArrayList<>();

        for (NotificationSendTask task : tasks) {
            try {
                task.future().join();
                successIds.add(task.event().id());
            } catch (Exception ex) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                failedReasons.put(task.event().id(), safeFailureReason(cause));
            }
        }

        // 최종 상태 DB 반영
        if (!successIds.isEmpty()) {
            repository.markSent(successIds);
        }

        if (!failedReasons.isEmpty()) {
            repository.markFailedWithReasons(failedReasons);
        }
    }

    private String safeFailureReason(Throwable ex) {
        if (ex == null) {
            return "UNKNOWN_ERROR";
        }

        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        String exceptionName = root.getClass().getSimpleName();
        String message = root.getMessage();

        String result;
        if (message == null || message.isBlank()) {
            result = exceptionName;
        } else {
            result = exceptionName + ": " + message;
        }

        if (result.length() > MAX_FAILURE_REASON_LENGTH) {
            result = result.substring(0, MAX_FAILURE_REASON_LENGTH);
        }

        return result;
    }
}
