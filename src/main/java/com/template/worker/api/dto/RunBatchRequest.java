package com.template.worker.api.dto;

import lombok.Getter;

import java.util.Map;

/**
 * Batch 실행 요청 DTO
 * - jobName: 실행할 job의 이름
 * - params: jobParameters로 전달될 파라미터 맵
 */
@Getter
public class RunBatchRequest {
    private String jobName;
    private Map<String, String> params;
}
