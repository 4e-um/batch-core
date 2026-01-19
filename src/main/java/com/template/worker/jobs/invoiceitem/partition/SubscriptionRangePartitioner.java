package com.template.worker.jobs.invoiceitem.partition;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionRangePartitioner implements Partitioner {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {

    Long min = jdbcTemplate.queryForObject("SELECT MIN(sub_id) FROM subscription", Long.class);
    Long max = jdbcTemplate.queryForObject("SELECT MAX(sub_id) FROM subscription", Long.class);

    long total = max - min + 1;
    long targetSize = Math.max(total / gridSize, 1);

    Map<String, ExecutionContext> result = new HashMap<>();

    long start = min;
    long end = start + targetSize - 1;

    for (int i = 0; i < gridSize; i++) {
      if (i == gridSize - 1) {
        end = max;
      }

      ExecutionContext context = new ExecutionContext();
      context.putLong("minValue", start);
      context.putLong("maxValue", end);

      result.put("partition" + i, context);

      start = end + 1;
      end = start + targetSize - 1;
    }

    return result;
  }
}
