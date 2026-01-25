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

        // invMonth가 없으면 통과 (Reader에서 현재 날짜를 기본값으로 사용함)
        if (invMonth == null || invMonth.trim().isEmpty()) {
            return;
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
