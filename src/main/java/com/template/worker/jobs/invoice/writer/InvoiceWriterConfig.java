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
public class InvoiceWriter {

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
                        sub_id,
                        inv_month,
                        total_amount,
                        total_discount,
                        total_price,
                        created_at
                    ) VALUES (
                        :subId,
                        :invMonth,
                        :totalAmount,
                        :totalDiscount,
                        :totalPrice,
                        now()
                    )
                """)
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("subId", item.getSubId());
                    params.addValue("invMonth", invMonth);
                    params.addValue("totalAmount", item.getTotalAmount());
                    params.addValue("totalDiscount", item.getTotalDiscount());
                    params.addValue("totalPrice", item.getTotalAmount()-item.getTotalDiscount());
                    return params;
                })
                .build();
    }
}