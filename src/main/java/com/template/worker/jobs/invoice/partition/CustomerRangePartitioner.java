package com.template.worker.jobs.invoice.partition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerRangePartitioner implements Partitioner {

    private final DataSource dataSource;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long minSubId;
        long maxSubId;

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

        // 데이터가 없는 경우 빈 맵 반환
        if (maxSubId == 0 && minSubId == 0) {
            return new HashMap<>();
        }

        // targetSize를 구할 때 나머지를 고려하여 계산
        long totalRange = maxSubId - minSubId + 1;
        long targetSize = totalRange / gridSize;

        Map<String, ExecutionContext> result = new HashMap<>();
        long currentStart = minSubId;

        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            long currentEnd = currentStart + targetSize - 1;

            // 마지막 파티션이거나, 계산된 end가 max를 넘어가면 max로 고정
            if (i == gridSize - 1 || currentEnd > maxSubId) {
                currentEnd = maxSubId;
            }

            context.putLong("minSubId", currentStart);
            context.putLong("maxSubId", currentEnd);
            result.put("partition" + i, context);

            // 중요: 다음 시작점은 현재 끝점의 바로 다음 번호로 설정 (빈틈 방지)
            currentStart = currentEnd + 1;

            // 만약 이미 maxId에 도달했다면 루프 종료 (데이터가 gridSize보다 적을 경우 대비)
            if (currentStart > maxSubId) {
                break;
            }
        }

        return result;
    }
}
