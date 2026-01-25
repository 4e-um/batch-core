package com.template.worker.jobs.invoice.reader;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.template.worker.jobs.invoice.model.InvoiceAggregationRow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InvoiceReader {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<InvoiceAggregationRow> invoicePagingReader(
            @Value("#{stepExecutionContext[minSubId]}") Long minSubId,
            @Value("#{stepExecutionContext[maxSubId]}") Long maxSubId,
            @Value("#{jobParameters[invMonth]}") String invMonth,
            @Value("${spring.batch.jobs.invoice.page-size}") int pageSize) {

        JdbcPagingItemReader<InvoiceAggregationRow> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setPageSize(pageSize);

        reader.setRowMapper(
                (rs, rowNum) -> {
                    InvoiceAggregationRow row = new InvoiceAggregationRow();
                    row.setSubId(rs.getLong("sub_id"));
                    row.setTotalAmount(rs.getLong("total_amount"));
                    row.setTotalDiscount(rs.getLong("total_discount"));
                    return row;
                });

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();

            queryProvider.setSelectClause(
                    """
                                SELECT
                                    sub_id,
                                    SUM(value) FILTER(WHERE value > 0) AS total_amount,
                                    SUM(ABS(value)) FILTER(WHERE value < 0) AS total_discount
                            """);

            queryProvider.setFromClause("""
                        FROM invoice_item
                    """);

            queryProvider.setWhereClause(
                    """
                                WHERE inv_month = :invMonth
                                  AND sub_id BETWEEN :minSubId AND :maxSubId
                                  AND value <> 0
                            """);

            // ⭐ GROUP BY = ORDER BY (paging 안정성 핵심)
            queryProvider.setGroupClause("""
                        GROUP BY sub_id
                    """);

            queryProvider.setSortKeys(Map.of("sub_id", Order.ASCENDING));

        reader.setQueryProvider(queryProvider);
        Map<String, Object> params = new HashMap<>();
        params.put("invMonth", invMonth);
        params.put("minSubId", minSubId);
        params.put("maxSubId", maxSubId);

        reader.setParameterValues(params);

        log.info("[Reader Param] invMonth = {}", invMonth);
        log.info("[Reader Param] minSubId = {}", minSubId);
        log.info("[Reader Param] maxSubId = {}", maxSubId);

        return reader;
    }
}
