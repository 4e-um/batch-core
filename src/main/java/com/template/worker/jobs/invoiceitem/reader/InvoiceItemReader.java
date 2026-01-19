package com.template.worker.jobs.invoiceitem.reader;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemAggregateRow;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemReader {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<InvoiceItemAggregateRow> reader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue,
            @Value("#{jobParameters['billingYm']}") String billingYm,
            @Value("${spring.batch.jobs.invoice-item.page-size}") int pageSize) {

        PagingQueryProvider queryProvider = pagingQueryProvider();

        return new JdbcPagingItemReaderBuilder<InvoiceItemAggregateRow>()
                .name("invoiceItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(Map.of(
                        "minValue", minValue,
                        "maxValue", maxValue,
                        "invMonth", billingYm
                ))
                .pageSize(pageSize)
                .rowMapper((rs, rowNum) -> new InvoiceItemAggregateRow(
                            rs.getLong("sub_id"),
                            rs.getString("inv_month"),
                            rs.getString("type"),
                            rs.getString("value_type"),
                            rs.getString("name"),
                            rs.getDouble("value"),
                            rs.getString("target_scope"),
                            rs.getLong("source_id")))
            .build();
    }

    private PagingQueryProvider pagingQueryProvider() {

        PostgresPagingQueryProvider provider = new PostgresPagingQueryProvider();

        provider.setSelectClause("SELECT sub_id, inv_month, type, value_type, name, value, target_scope, source_id");
        provider.setFromClause("FROM ( " + fullUnionSql() + " ) t");
        provider.setWhereClause("WHERE t.sub_id BETWEEN :minValue AND :maxValue");
        provider.setSortKeys((Map.of("sub_id", Order.ASCENDING,
                                      "type", Order.ASCENDING,
                                      "source_id", Order.ASCENDING)));

        return provider;
    }

    private String fullUnionSql() {
        return planSql()
                + "\n UNION ALL \n"
                + vasSql()
                + "\n UNION ALL \n"
                + microPaymentSql()
                + "\n UNION ALL \n"
                + discountSql();
    }

    // 요금제 SQL
    private String planSql() {
        return """
                SELECT
                    sp.sub_id AS sub_id,
                    :invMonth AS inv_month,
                    'PLAN' AS type,
                    'FIXED' AS value_type,
                    p.plan_name AS name,
                    sp.cost AS value,
                    NULL AS target_scope,
                    sp.sp_id AS source_id
                FROM subscription_plan sp
                JOIN plan p ON sp.plan_id = p.plan_id
                WHERE
                    sp.created_date <= (
                        date_trunc('month', to_date(:invMonth, 'YYYYMM'))
                        - interval '1 second'
                    )
                AND sp.left_date >= (
                     date_trunc(
                         'month',
                         to_date(:invMonth, 'YYYYMM') - interval '1 month'
                     )
                )
                """;
    }

    // 부가서비스 SQL
    private String vasSql() {
        return """
                SELECT
                    sv.sub_id AS sub_id,
                    :invMonth AS inv_month,
                    'VAS' AS type,
                    'FIXED' AS value_type,
                    v.name AS name,
                    sv.monthly_fee AS value,
                    NULL AS target_scope,
                    sv.sv_id AS source_id
                FROM subscription_vas sv
                JOIN vas v ON sv.vas_id = v.vas_id
                WHERE
                    sv.start_date <= (
                        date_trunc('month', to_date(:invMonth, 'YYYYMM'))
                        - interval '1 second'
                    )
                AND (
                    sv.end_date IS NULL
                    OR sv.end_date >= (
                        date_trunc(
                            'month',
                            to_date(:invMonth, 'YYYYMM') - interval '1 month'
                        )
                    )
                )
                """;
    }

    // 소액 결제 SQL
    private String microPaymentSql() {
        return """
                SELECT
                    mp.sub_id AS sub_id,
                    :invMonth AS inv_month,
                    'MICRO' AS type,
                    'FIXED' AS value_type,
                    mp.name AS name,
                    mp.amount AS value,
                    NULL AS target_scope,
                    mp.micro_id AS source_id
                FROM micro_payment mp
                WHERE
                    mp.pay_date >= date_trunc(
                        'month',
                        to_date(:invMonth, 'YYYYMM') - interval '1 month'
                    )
                AND mp.pay_date < date_trunc(
                        'month',
                        to_date(:invMonth, 'YYYYMM')
                    )
                AND mp.status = 'BILLED'
                """;
    }

    // 할인 SQL
    private String discountSql() {
        return """
                SELECT
                    sd.sub_id AS sub_id,
                    :invMonth AS inv_month,
                    'DISCOUNT' AS type,
                    sd.discount_type AS value_type,
                    dp.name AS name,
                    CASE
                        WHEN sd.discount_type = 'RATE'
                        AND sd.target_scope = 'PLAN_FEE'
                        THEN (sp.cost * sd.value * -1)
                        ELSE (sd.value * -1)
                    END AS value,
                    sd.target_scope AS target_scope,
                    sd.sd_id AS source_id
                FROM subscription_discount sd
                JOIN discount_policy dp ON sd.discount_id = dp.discount_id
                LEFT JOIN subscription_plan sp ON sd.sub_id = sp.sub_id
                AND sp.created_date <= (
                        date_trunc('month', to_date(:invMonth, 'YYYYMM'))
                        - interval '1 second'
                    )
                WHERE
                    sd.start_date <= (
                        date_trunc('month', to_date(:invMonth, 'YYYYMM'))
                        - interval '1 second'
                    )
                AND (
                    sd.end_date IS NULL
                    OR sd.end_date >= (
                        date_trunc(
                            'month',
                            to_date(:invMonth, 'YYYYMM') - interval '1 month'
                        )
                    )
                )
                """;
    }
}