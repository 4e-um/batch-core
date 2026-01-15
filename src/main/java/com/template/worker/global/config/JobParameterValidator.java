package com.template.worker.global.config;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobParameterValidator implements JobParametersValidator {

    private static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {

        String billingYm = parameters.getString("billingYm");

        if (billingYm == null) {
            throw new JobParametersInvalidException("billingYm is required. (format: yyyyMM)");
        }

        try {
            YearMonth ym = YearMonth.parse(billingYm, YM_FORMATTER);

            if (ym.isAfter(YearMonth.now().plusMonths(1))) {
                throw new JobParametersInvalidException("billingYm cannot be future month.");
            }

        } catch (java.time.format.DateTimeParseException e) {
            throw new JobParametersInvalidException("Invalid billingYm format. Required: yyyyMM");
        }
    }
}