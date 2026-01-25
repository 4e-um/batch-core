package com.template.worker.jobs.invoiceitem.reader;

import java.time.LocalDate;
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
    public JdbcPagingItemReader<InvoiceItemAggregateRow> planDiscountReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue,
            @Value("#{jobParameters['invMonth']}") String invMonth,
            @Value("${spring.batch.jobs.invoice-item.page-size}") int pageSize) {
        return buildReader(
                minValue,
                maxValue,
                invMonth,
                pageSize,
                queryProvider.planAndDiscountSql(),
                "planDiscountReader");
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<InvoiceItemAggregateRow> microPaymentReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue,
            @Value("#{jobParameters['invMonth']}") String invMonth,
            @Value("${spring.batch.jobs.invoice-item.page-size}") int pageSize) {
        return buildReader(
                minValue,
                maxValue,
                invMonth,
                pageSize,
                queryProvider.microPaymentQuery(),
                "microPaymentReader");
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<InvoiceItemAggregateRow> vasReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue,
            @Value("#{jobParameters['invMonth']}") String invMonth,
            @Value("${spring.batch.jobs.invoice-item.page-size}") int pageSize) {
        return buildReader(
                minValue, maxValue, invMonth, pageSize, queryProvider.vasQuery(), "vasReader");
    }

    private JdbcPagingItemReader<InvoiceItemAggregateRow> buildReader(
            Long minValue, Long maxValue, String invMonth, int pageSize, String sql, String name) {

        // invMonth가 null일 경우 현재 날짜(yyyyMM) 사용
        String effectiveInvMonth = invMonth;
        if (effectiveInvMonth == null || effectiveInvMonth.trim().isEmpty()) {
            effectiveInvMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        }

        YearMonth yearMonth =
                YearMonth.parse(effectiveInvMonth, DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDateTime startOfBillingPeriod = yearMonth.minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime endOfBillingPeriod = yearMonth.atDay(1).atStartOfDay();

        PagingQueryProvider pqueryProvider = pagingQueryProvider(sql);

        return new JdbcPagingItemReaderBuilder<InvoiceItemAggregateRow>()
                .name(name)
                .dataSource(dataSource)
                .queryProvider(pqueryProvider)
                .parameterValues(
                        Map.of(
                                "minValue", minValue,
                                "maxValue", maxValue,
                                "invMonth", effectiveInvMonth,
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

    private PagingQueryProvider pagingQueryProvider(String sql) {

        PostgresPagingQueryProvider provider = new PostgresPagingQueryProvider();

        provider.setSelectClause(
                "SELECT sub_id, inv_month, type, value_type, name, value, target_scope, source_id");
        provider.setFromClause("FROM ( " + sql + " ) t");
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
