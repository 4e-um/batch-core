package com.template.worker;

import com.template.worker.global.runner.BatchJobRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class WorkerApplicationTests {

  @MockitoBean private BatchJobRunner batchJobRunner;

  @Test
  void contextLoads() {}
}
