package com.template.worker.batch.orchestrator;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchTimeWindowService {

    private final NamedParameterJdbcTemplate jdbc;

    public LocalDateTime resolve(String jobName) {
        return jdbc.queryForObject(
                """
                        SELECT last_processed_at
                        FROM batch_watermark
                        WHERE job_name = :jobName
                        """,
                Map.of("jobName", jobName),
                LocalDateTime.class);
    }

    @Transactional
    public void update(String jobName, LocalDateTime newFrom) {
        jdbc.update(
                """
                UPDATE batch_watermark
                SET last_processed_at = :newFrom
                WHERE job_name = :jobName
                """,
                Map.of(
                        "jobName", jobName,
                        "newFrom", newFrom));
    }
}
