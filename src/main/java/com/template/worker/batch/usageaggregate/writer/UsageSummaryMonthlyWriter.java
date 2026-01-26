package com.template.worker.batch.usageaggregate.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.template.worker.batch.usageaggregate.config.Sqls;
import com.template.worker.batch.usageaggregate.dto.UsageMonthlyAggregation;
import com.template.worker.batch.usageaggregate.dto.UsageMonthlyKey;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsageSummaryMonthlyWriter implements ItemWriter<UsageMonthlyAggregation> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends UsageMonthlyAggregation> chunk) {

        Map<UsageMonthlyKey, Long> aggregated = new HashMap<>();

        for (UsageMonthlyAggregation item : chunk) {
            UsageMonthlyKey key = new UsageMonthlyKey(item.subId(), item.period());

            aggregated.merge(key, item.deltaBytes(), Long::sum);
        }

        if (aggregated.isEmpty()) {
            return;
        }

        String sql = Sqls.MONTHLY_UPSERT;

        List<Map<String, Object>> params =
                aggregated.entrySet().stream()
                        .map(
                                entry -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("subId", entry.getKey().subId());
                                    map.put("period", entry.getKey().period());
                                    map.put("delta", entry.getValue());
                                    return map;
                                })
                        .toList();

        jdbcTemplate.batchUpdate(sql, params.toArray(new Map[0]));
    }
}
