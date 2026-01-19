package com.template.worker.jobs.invoice.partition;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomerRangePartitioner implements Partitioner {

    private final DataSource dataSource;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        Long minSubId;
        Long maxSubId;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("select min(sub_id), max(sub_id) from invoice_item");
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            minSubId = rs.getLong(1);
            maxSubId = rs.getLong(2);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        long targetSize = (maxSubId - minSubId + 1) / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<>();

        long start = minSubId;
        long end = start + targetSize - 1;

        for (int i = 0; i < gridSize; i++) {

            // 파티션 나누고 남은 데이터를 마지막 파티션에서 작업
            if(i == gridSize - 1){
                end = maxSubId;
            }

            ExecutionContext context = new ExecutionContext();
            context.putLong("minSubId", start);
            context.putLong("maxSubId", Math.min(end, maxSubId));

            result.put("partition" + i, context);

            start += targetSize;
            end += targetSize;
        }

        return result;
    }
}
