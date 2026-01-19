package com.template.worker.jobs.invoice.writer;

import com.template.worker.jobs.invoice.model.InvoiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class InvoiceWriterConfig {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcBatchItemWriter<InvoiceEntity> invoiceWriter(
            @Value("#{jobParameters[invMonth]}") String invMonth
    ) {

        return new JdbcBatchItemWriterBuilder<InvoiceEntity>()
                .dataSource(dataSource)
                .sql("""
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
                                to_date(:invMonth,'YYYYMM'),
                                to_date(:invMonth,'YYYYMM') + interval '1 month' - interval '1 second',
                                to_date(:invMonth,'YYYYMM') + interval '1 month' - interval '1 second',
                                now() AT TIME ZONE 'Asia/Seoul'
                            FROM subscription s
                            JOIN customer c
                            ON s.customer_id = c.customer_id
                            WHERE s.sub_id = :subId
                            ON CONFLICT (sub_id, inv_month) DO NOTHING;
                """)
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("subId", item.getSubId());
                    params.addValue("invMonth", invMonth);
                    params.addValue("totalAmount", item.getTotalAmount());
                    params.addValue("totalDiscount", item.getTotalDiscount());
                    params.addValue("totalPrice", item.getTotalPrice());
                    return params;
                })
                .build();
    }
}