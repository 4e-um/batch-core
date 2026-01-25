package com.template.worker.jobs.invoiceitem.writer;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemRecord;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemWriter {

    private final DataSource dataSource;

    @Bean
    public JdbcBatchItemWriter<InvoiceItemRecord> writer() {
        return new JdbcBatchItemWriterBuilder<InvoiceItemRecord>()
                .dataSource(dataSource)
                .sql(
                        """
                    INSERT INTO invoice_item (
                        sub_id,
                        inv_month,
                        name,
                        type,
                        value_type,
                        value,
                        target_scope,
                        created_at,
                        source_id
                    ) VALUES (
                        :subId,
                        :invMonth,
                        :name,
                        :type,
                        :valueType,
                        :value,
                        :targetScope,
                        :createdAt,
                        :sourceId
                    )
                    ON CONFLICT (source_id, type, inv_month) DO NOTHING
                """)
                .beanMapped()
                .assertUpdates(false)
                .build();
    }
}
