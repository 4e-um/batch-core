package com.template.worker.batch.usagenotification.config.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.template.worker.batch.usagenotification.dto.UsageNotificationCandidate;
import com.template.worker.batch.usagenotification.dto.UsageNotificationSource;

@Component
public class UsageNotificationPolicy {

    public Optional<UsageNotificationCandidate> evaluate(UsageNotificationSource source) {
        if (source.allotmentAmount() <= 0) {
            return Optional.empty();
        }

        long totalUsedMb = source.totalUsedBytes() / (1024 * 1024);
        long allotmentMb = source.allotmentAmount();

        double percent = (double) (totalUsedMb * 100L) / allotmentMb;

        return decideThreshold((int) percent)
                .map(
                        threshold ->
                                new UsageNotificationCandidate(
                                        source.subId(),
                                        source.period(),
                                        source.unit(),
                                        source.planName(),
                                        threshold,
                                        percent,
                                        totalUsedMb,
                                        allotmentMb));
    }

    private Optional<Integer> decideThreshold(int percent) {
        if (percent >= 100) {
            return Optional.of(100);
        }
        if (percent >= 80) {
            return Optional.of(80);
        }
        if (percent >= 50) {
            return Optional.of(50);
        }
        return Optional.empty();
    }
}
