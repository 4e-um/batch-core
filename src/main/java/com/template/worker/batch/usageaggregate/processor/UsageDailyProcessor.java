package com.template.worker.batch.usageaggregate.processor;

import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.batch.usageaggregate.dto.UsageDailyAggregation;
import com.template.worker.batch.usageaggregate.dto.UsageLogRow;

@Component
public class UsageDailyProcessor implements ItemProcessor<UsageLogRow, UsageDailyAggregation> {

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public UsageDailyAggregation process(UsageLogRow log) {
        return new UsageDailyAggregation(
                log.subId(), log.eventTime().format(DAY_FMT), log.usedBytes());
    }
}
