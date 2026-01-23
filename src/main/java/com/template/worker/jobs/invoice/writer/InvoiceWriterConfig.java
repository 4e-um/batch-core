package com.template.worker.jobs.invoice.writer;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.template.worker.jobs.invoice.model.InvoiceEntity;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InvoiceWriterConfig {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcBatchItemWriter<InvoiceEntity> invoiceWriter(
            @Value("#{jobParameters[invMonth]}") String invMonth) {
        return new JdbcBatchItemWriterBuilder<InvoiceEntity>()
                .dataSource(dataSource)
                .sql(
                        """
                    INSERT INTO invoice (
                                inv_no,
                                sub_id,
                                name,
                                phone_enc,
                                email_enc,
                                inv_month,
                                total_amount,
                                total_discount,
                                total_price,
                                start_date,
                                end_date,
                                due_date,
                                created_at
                            )
                            SELECT
                                nextval('invoice_no_seq'),
                                s.sub_id,
                                c.name,
                                c.contact_enc,
                                c.email_enc,
                                :invMonth,
                                :totalAmount,
                                :totalDiscount,
                                :totalPrice,
                                -- 시작일: 전월 1일
                                to_date(:invMonth, 'YYYYMM') - interval '1 month',
                                -- 마감일: 전월 말일 23:59:59
                                to_date(:invMonth, 'YYYYMM') - interval '1 second',
                                -- 납기일: 당월 말일 23:59:59
                                to_date(:invMonth, 'YYYYMM') + interval '1 month' - interval '1 second',
                                now()
                            FROM subscription s
                            JOIN customer c
                            ON s.customer_id = c.customer_id
                            WHERE s.sub_id = :subId
                            -- 중복 방지 (sub_id + inv_month 조합)
                            ON CONFLICT (sub_id, inv_month) DO NOTHING;
                """)
                .itemSqlParameterSourceProvider(
                        item -> {
                            MapSqlParameterSource params = new MapSqlParameterSource();
                            params.addValue("subId", item.getSubId());
                            params.addValue("invMonth", invMonth);
                            params.addValue("totalAmount", item.getTotalAmount());
                            params.addValue("totalDiscount", item.getTotalDiscount());
                            params.addValue("totalPrice", item.getTotalPrice());
                            return params;
                        })
                .assertUpdates(false) // ON CONFLICT로 인해 0건 반영되어도 예외 무시
                .build();
    }
}
