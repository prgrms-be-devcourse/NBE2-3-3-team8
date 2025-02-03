package org.programmers.signalbuddy.domain.like.batch

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.global.support.BatchTest
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired

internal class LikeBatchLogJobConfigTest : BatchTest() {
    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var likeBatchLogDeleteJob: Job

    @Test
    @Throws(Exception::class)
    fun `좋아요 배치 작업의 로그 삭제 잡 실행`() {
        // when
        jobLauncherTestUtils.job = likeBatchLogDeleteJob
        val jobExecution = jobLauncherTestUtils.launchJob()

        // then
        Assertions.assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
    }
}