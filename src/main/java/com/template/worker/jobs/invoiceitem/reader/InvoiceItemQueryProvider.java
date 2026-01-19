package com.template.worker.jobs.invoiceitem.reader;

import org.springframework.stereotype.Component;

@Component
public class InvoiceItemQueryProvider {

    public String fullUnionSql() {
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
                WHERE sp.created_date < :endOfBillingPeriod
                  AND sp.left_date >= :startOfBillingPeriod
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
                WHERE sv.start_date < :endOfBillingPeriod
                  AND (sv.end_date IS NULL OR sv.end_date >= :startOfBillingPeriod)
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
                WHERE mp.pay_date >= :startOfBillingPeriod
                  AND mp.pay_date < :endOfBillingPeriod
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
                        AND sp.cost IS NOT NULL
                        THEN (sp.cost * sd.value * -1)
                        ELSE (sd.value * -1)
                    END AS value,
                    sd.target_scope AS target_scope,
                    sd.sd_id AS source_id
                FROM subscription_discount sd
                JOIN discount_policy dp ON sd.discount_id = dp.discount_id
                LEFT JOIN subscription_plan sp
                       ON sd.sub_id = sp.sub_id
                      AND sp.created_date < :endOfBillingPeriod
                WHERE sd.start_date < :endOfBillingPeriod
                  AND (sd.end_date IS NULL OR sd.end_date >= :startOfBillingPeriod)
                """;
    }
}
