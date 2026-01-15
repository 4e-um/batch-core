package com.template.worker.global.launcher;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

/** - 운영 시스템은 이 클래스를 통해 Job을 실행한다. */
@Component
@RequiredArgsConstructor
public class BatchJobLauncher {
  private final JobLauncher jobLauncher;

  public void launch(Job job, JobParameters params) throws Exception {
    jobLauncher.run(job, params);
  }
}