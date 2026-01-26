package com.template.worker.batch.usagenotification.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.template.worker.batch.usagenotification.dto.UsageNotificationCandidate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsageNotificationWriter implements ItemWriter<UsageNotificationCandidate> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void write(Chunk<? extends UsageNotificationCandidate> chunk) {

        if (chunk.isEmpty()) {
            return;
        }

        String sql =
                """
                INSERT INTO usage_notification_outbox
                    (sub_id, period,
                     plan_name, unit,
                     threshold, percent,
                     total_used_mb, allotment_mb,
                     status, created_at)
                VALUES
                    (:subId, :period,
                     :planName, :unit,
                     :threshold, :percent,
                     :totalUsedMb, :allotmentMb,
                     'PENDING', NOW())
                ON CONFLICT (sub_id, period, unit, threshold)
                DO NOTHING
                """;

        List<Map<String, Object>> paramList =
                chunk.getItems().stream()
                        .map(
                                c -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("subId", c.subId());
                                    map.put("period", c.period());
                                    map.put("planName", c.planName());
                                    map.put("unit", c.unit());
                                    map.put("threshold", c.threshold());
                                    map.put("percent", c.percent());
                                    map.put("totalUsedMb", c.totalUsedMb());
                                    map.put("allotmentMb", c.allotmentMb());
                                    return map;
                                })
                        .toList();

        jdbcTemplate.batchUpdate(sql, paramList.toArray(new Map[0]));
    }
}
