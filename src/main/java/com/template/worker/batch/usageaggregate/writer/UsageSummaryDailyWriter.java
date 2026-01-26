package com.template.worker.batch.usageaggregate.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.template.worker.batch.usageaggregate.config.Sqls;
import com.template.worker.batch.usageaggregate.dto.UsageDailyAggregation;
import com.template.worker.batch.usageaggregate.dto.UsageDailyKey;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsageSummaryDailyWriter implements ItemWriter<UsageDailyAggregation> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends UsageDailyAggregation> chunk) {
        Map<UsageDailyKey, Long> aggregated = new HashMap<>();

        for (UsageDailyAggregation item : chunk) {
            UsageDailyKey key = new UsageDailyKey(item.subId(), item.usageDate());

            aggregated.merge(key, item.deltaBytes(), Long::sum);
        }

        if (aggregated.isEmpty()) {
            return;
        }

        String sql = Sqls.DAILY_UPSERT;

        List<Map<String, Object>> params =
                aggregated.entrySet().stream()
                        .map(
                                e -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("subId", e.getKey().subId());
                                    map.put("usageDate", e.getKey().usageDate());
                                    map.put("delta", e.getValue());
                                    return map;
                                })
                        .toList();

        jdbcTemplate.batchUpdate(sql, params.toArray(new Map[0]));
    }
}
