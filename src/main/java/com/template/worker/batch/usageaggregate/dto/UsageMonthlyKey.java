package com.template.worker.batch.usageaggregate.dto;

import java.util.Objects;

public record UsageMonthlyKey(Long subId, String period) {

    @Override
    public boolean equals(Object oj) {
        if (this == oj) {
            return true;
        }
        if (!(oj instanceof UsageMonthlyKey that)) {
            return false;
        }
        return Objects.equals(subId, that.subId) && Objects.equals(period, that.period);
    }
}
