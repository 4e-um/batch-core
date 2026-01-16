package com.template.worker.jobs.invoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

@ActiveProfiles("test")          // test 프로파일로 실행 (배치 메타 테이블 초기화용)
@SpringBatchTest                 // Spring Batch 테스트 유틸 활성화
@SpringBootTest                  // 전체 ApplicationContext 로딩
class InvoiceExecutionJobTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils; // Batch Job을 테스트용으로 실행해주는 유틸

    @Autowired
    @Qualifier("invoiceJob")// 실제 실행할 Job Bean
    Job invoiceExecutionJob;

    @Autowired
    JdbcTemplate jdbcTemplate;// DB 결과 검증용

    @BeforeEach
    void beforeEach() {
        // 테스트마다 실행할 Job을 JobLauncherTestUtils에 설정
        jobLauncherTestUtils.setJob(invoiceExecutionJob);
    }

    @Test
    void invoiceExecutionJobTest() throws Exception {

        // when: Job 실행
        JobExecution jobExecution =
                jobLauncherTestUtils.launchJob(buildJobParameters());

        System.out.println("Job Status = " + jobExecution.getStatus());
        System.out.println("Exit Status = " + jobExecution.getExitStatus());


        // 실행된 Step 하나 가져오기 (단일 Step 기준)
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();

        assertThat(stepExecutions).isNotEmpty();

        StepExecution stepExecution = stepExecutions.iterator().next();

        // then 1️⃣ Job 자체가 정상 종료되었는지
        assertThat(jobExecution.getStatus())
                .isEqualTo(BatchStatus.COMPLETED);

        // then 2️⃣ Reader가 실제로 데이터를 읽었는지
        assertThat(stepExecution.getReadCount())
                .isGreaterThan(0);

        // then 3️⃣ DB에 청구서 데이터가 실제로 저장되었는지
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM invoice WHERE inv_month = '202508'",
                Integer.class
        );

        assertThat(count).isGreaterThan(0);
    }

    /**
     * 테스트용 JobParameters 생성
     * - invMonth : Job/Reader에서 사용하는 핵심 파라미터
     * - run.id   : 매 실행마다 다른 JobInstance 생성을 위한 값
     */
    private JobParameters buildJobParameters() {
        return new JobParametersBuilder()
                .addLong("invMonth", 202508L)          // 청구 대상 월
                .addLong("run.id", System.currentTimeMillis()) // 재실행 방지용
                .toJobParameters();
    }
}

