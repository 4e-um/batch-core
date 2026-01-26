package com.template.worker.batch.usageaggregate.reader;

import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.template.worker.batch.usageaggregate.dto.UsageLogRow;

@Configuration
public class UsageLogDailyReaderConfig {

    @Value("${spring.batch.jobs.usage.fetch-size}")
    private int fetchSize;

    @Bean(name = "usageLogDailyReader")
    @StepScope
    public JdbcCursorItemReader<UsageLogRow> usageLogDailyReader(
            DataSource dataSource,
            @Value("#{jobParameters['fromTime']}") LocalDateTime fromTime,
            @Value("#{jobParameters['toTime']}") LocalDateTime toTime) {
        String sql =
                """
                    SELECT
                        ul.sub_id,
                        ul.used_bytes,
                        ul.event_time
                    FROM usage_log ul
                    JOIN subscription_plan sp
                      ON sp.sub_id = ul.sub_id
                    WHERE ul.event_time >= ?
                      AND ul.event_time < ?
                      AND sp.allotment_amount = 5120
                    ORDER BY ul.id
                    """;

        return new JdbcCursorItemReaderBuilder<UsageLogRow>()
                .name("usageLogDailyReader")
                .dataSource(dataSource)
                .sql(sql)
                .fetchSize(fetchSize)
                .preparedStatementSetter(
                        ps -> {
                            ps.setObject(1, fromTime);
                            ps.setObject(2, toTime);
                        })
                .rowMapper(
                        (rs, rowNum) ->
                                new UsageLogRow(
                                        rs.getLong("sub_id"),
                                        rs.getLong("used_bytes"),
                                        rs.getTimestamp("event_time").toLocalDateTime()))
                .build();
    }
}
