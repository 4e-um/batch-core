package com.template.worker.jobs.invoiceitem.reader;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemAggregateRow;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InvoiceItemReader {

    private final DataSource dataSource;
    private final InvoiceItemQueryProvider queryProvider;

    @Bean
    @StepScope
    public JdbcPagingItemReader<InvoiceItemAggregateRow> reader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue,
            @Value("#{jobParameters['invMonth']}") String invMonth,
            @Value("${spring.batch.jobs.invoice-item.page-size}") int pageSize) {

        YearMonth yearMonth = YearMonth.parse(invMonth, DateTimeFormatter.ofPattern("yyyyMM"));

        LocalDateTime startOfBillingPeriod = yearMonth.minusMonths(1).atDay(1).atStartOfDay();

        LocalDateTime endOfBillingPeriod = yearMonth.atDay(1).atStartOfDay();

        PagingQueryProvider queryProvider = pagingQueryProvider();

        return new JdbcPagingItemReaderBuilder<InvoiceItemAggregateRow>()
                .name("invoiceItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(
                        Map.of(
                                "minValue", minValue,
                                "maxValue", maxValue,
                                "invMonth", invMonth,
                                "startOfBillingPeriod", startOfBillingPeriod,
                                "endOfBillingPeriod", endOfBillingPeriod))
                .pageSize(pageSize)
                .rowMapper(
                        (rs, rowNum) ->
                                new InvoiceItemAggregateRow(
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

        provider.setSelectClause(
                "SELECT sub_id, inv_month, type, value_type, name, value, target_scope, source_id");
        provider.setFromClause("FROM ( " + queryProvider.fullUnionSql() + " ) t");
        provider.setWhereClause("WHERE t.sub_id BETWEEN :minValue AND :maxValue");
        provider.setSortKeys(
                (Map.of(
                        "sub_id",
                        Order.ASCENDING,
                        "type",
                        Order.ASCENDING,
                        "source_id",
                        Order.ASCENDING)));

        return provider;
    }
}
