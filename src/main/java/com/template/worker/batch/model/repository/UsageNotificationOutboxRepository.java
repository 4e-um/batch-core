package com.template.worker.batch.model.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UsageNotificationOutboxRepository {

    private final JdbcTemplate jdbcTemplate;

    public void markProcessing(List<Long> ids) {
        jdbcTemplate.batchUpdate(
                "UPDATE usage_notification_outbox SET status = 'PROCESSING' WHERE id = ?",
                ids,
                ids.size(),
                (ps, id) -> ps.setLong(1, id));
    }

    public void markSent(List<Long> ids) {
        jdbcTemplate.batchUpdate(
                "UPDATE usage_notification_outbox SET status = 'SENT', sent_at = now() WHERE id ="
                        + " ?",
                ids,
                ids.size(),
                (ps, id) -> ps.setLong(1, id));
    }

    public void markFailedWithReasons(Map<Long, String> reasons) {
        jdbcTemplate.batchUpdate(
                "UPDATE usage_notification_outbox "
                        + "SET status = 'FAILED', failure_reason = ? "
                        + "WHERE id = ?",
                reasons.entrySet(),
                reasons.size(),
                (ps, entry) -> {
                    ps.setString(1, entry.getValue());
                    ps.setLong(2, entry.getKey());
                });
    }
}
