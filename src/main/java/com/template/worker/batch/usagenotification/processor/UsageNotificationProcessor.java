package com.template.worker.batch.usagenotification.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.template.worker.batch.usagenotification.config.util.UsageNotificationPolicy;
import com.template.worker.batch.usagenotification.dto.UsageNotificationCandidate;
import com.template.worker.batch.usagenotification.dto.UsageNotificationSource;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsageNotificationProcessor
        implements ItemProcessor<UsageNotificationSource, UsageNotificationCandidate> {

    private final UsageNotificationPolicy policy;

    @Override
    public UsageNotificationCandidate process(UsageNotificationSource source) {

        return policy.evaluate(source).orElse(null);
    }
}
