package com.template.worker.batch.notificationsend.reader;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.template.worker.batch.usagenotification.dto.UsageNotificationOutboxRow;

@Configuration
public class NotificationSendReaderConfig {

    @Bean(name = "notificationSendOutboxReader")
    public JdbcCursorItemReader<UsageNotificationOutboxRow> notificationSendOutboxReader(
            DataSource dataSource) {

        String sql =
                """
                    SELECT
                        o.id,
                        o.sub_id,
                        o.period,
                        o.plan_name,
                        o.threshold,
                        o.percent,
                        o.total_used_mb,
                        o.allotment_mb,
                        s.phone_number,
                        c.email_enc,
                        o.created_at
                    FROM usage_notification_outbox o
                    JOIN subscription s
                      ON s.sub_id = o.sub_id
                    JOIN customer c
                      ON c.customer_id = s.customer_id
                    WHERE o.status = 'PENDING'
                    ORDER BY o.id
                    """;

        return new JdbcCursorItemReaderBuilder<UsageNotificationOutboxRow>()
                .name("notificationSendOutboxReader")
                .dataSource(dataSource)
                .sql(sql)
                .fetchSize(1_000)
                .rowMapper(
                        (rs, rowNum) ->
                                new UsageNotificationOutboxRow(
                                        rs.getLong("id"),
                                        rs.getLong("sub_id"),
                                        rs.getString("period"),
                                        rs.getString("plan_name"),
                                        rs.getInt("threshold"),
                                        rs.getDouble("percent"),
                                        rs.getLong("total_used_mb"),
                                        rs.getLong("allotment_mb"),
                                        rs.getString("phone_number"),
                                        rs.getString("email_enc"),
                                        rs.getTimestamp("created_at").toLocalDateTime()))
                .build();
    }
}
