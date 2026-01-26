package com.template.worker.batch.usageaggregate.config;

public class Sqls {

    private Sqls() {}

    public static final String DAILY_UPSERT =
            """
            INSERT INTO usage_summary_daily (
                sub_id, usage_date, total_used_bytes, updated_at
            )
            VALUES (
                :subId, :usageDate, :delta, NOW()
            )
            ON CONFLICT (sub_id, usage_date)
            DO UPDATE SET
                total_used_bytes =
                    usage_summary_daily.total_used_bytes
                    + EXCLUDED.total_used_bytes,
                updated_at = NOW()
            """;

    public static final String MONTHLY_UPSERT =
            """
            INSERT INTO usage_summary_monthly (
                sub_id, period, total_used_bytes, updated_at
            )
            VALUES (
                :subId, :period, :delta, NOW()
            )
            ON CONFLICT (sub_id, period)
            DO UPDATE SET
                total_used_bytes =
                    usage_summary_monthly.total_used_bytes
                    + EXCLUDED.total_used_bytes,
                updated_at = NOW()
            """;
}
