package org.programmers.signalbuddy.global.batch.job

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.global.support.BatchTest
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired

internal class BatchLogJobTest : BatchTest() {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var oldBatchLogDeleteJob: Job

    @Test
    @Throws(Exception::class)
    fun `14일 이상된 배치 작업의 로그를 삭제하는 잡 실행`() {
        // when
        jobLauncherTestUtils.job = oldBatchLogDeleteJob
        val jobExecution = jobLauncherTestUtils.launchJob()

        // then
        Assertions.assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
    }
}