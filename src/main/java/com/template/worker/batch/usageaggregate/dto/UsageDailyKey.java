package com.template.worker.batch.usageaggregate.dto;

import java.util.Objects;

public record UsageDailyKey(Long subId, String usageDate) {
    @Override
    public boolean equals(Object oj) {
        if (this == oj) {
            return true;
        }
        if (!(oj instanceof UsageDailyKey that)) {
            return false;
        }
        return Objects.equals(subId, that.subId) && Objects.equals(usageDate, that.usageDate);
    }
}
