package com.template.worker.global.config;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JobParameterValidator implements JobParametersValidator {

    private static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {

        String invMonth = parameters.getString("invMonth");

        if (invMonth == null) {
            throw new JobParametersInvalidException("invMonth is required. (format: yyyyMM)");
        }

        try {
            YearMonth ym = YearMonth.parse(invMonth, YM_FORMATTER);

            if (ym.isAfter(YearMonth.now().plusMonths(1))) {
                throw new JobParametersInvalidException("invMonth cannot be future month.");
            }

        } catch (java.time.format.DateTimeParseException e) {
            throw new JobParametersInvalidException("Invalid invMonth format. Required: yyyyMM");
        }
    }
}
