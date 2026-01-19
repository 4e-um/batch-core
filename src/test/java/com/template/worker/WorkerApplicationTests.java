package com.template.worker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.template.worker.global.runner.BatchJobRunner;

@SpringBootTest
@ActiveProfiles("test")
class WorkerApplicationTests {

    @MockitoBean private BatchJobRunner batchJobRunner;

    @Test
    void contextLoads() {
        // Spring Boot application context loads successfully
    }
}
