package com.template.worker.jobs.invoiceSend.reader;

import com.template.worker.jobs.invoiceSend.model.InvoiceSendRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class InvoiceSendReader {

    @Bean
    @StepScope
    public JdbcCursorItemReader<InvoiceSendRecord> invoiceJoinReader(
            DataSource dataSource,
            @Value("#{jobParameters['invMonth']}") String invMonth
    ) {
        JdbcCursorItemReader<InvoiceSendRecord> reader =
                new JdbcCursorItemReader<>();

        reader.setDataSource(dataSource);
        reader.setSql("""
        SELECT
            i.inv_id,
            i.inv_no,
            i.sub_id,
            i.inv_month,
            i.phone_enc,
            i.email_enc,
            i.name,
            i.total_price,
            i.created_at,
            i.due_date,
            ii.item_id AS inv_item_id,
            ii.name    AS item_name,
            ii.type    AS item_type,
            ii.value   AS item_value
        FROM invoice i
        JOIN invoice_item ii
          ON i.sub_id = ii.sub_id
         AND i.inv_month = ii.inv_month
         AND ii.inv_month = ?
        WHERE i.inv_month = ?
        ORDER BY i.inv_id
    """);

        reader.setPreparedStatementSetter(ps -> {
                    ps.setString(1, invMonth);
                    ps.setString(2, invMonth);
        });
        reader.setFetchSize(1000);

        reader.setRowMapper(new DataClassRowMapper<>(InvoiceSendRecord.class));

        return reader;
    }
}
