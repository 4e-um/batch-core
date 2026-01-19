package com.template.worker.jobs.invoiceitem.writer;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemRecord;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                    INSERT INTO invoice_item_test (
                        sub_id,
                        inv_month,
                        name,
                        type,
                        value_type,
                        value,
                        target_scope,
                        created_at
                    ) VALUES (
                        :subId,
                        :invMonth,
                        :name,
                        :type,
                        :valueType,
                        :value,
                        :targetScope,
                        :createdAt
                    )
                """)
        .beanMapped()
        .assertUpdates(false)
        .build();
  }
}
