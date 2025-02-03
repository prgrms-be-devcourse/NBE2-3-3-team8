package org.programmers.signalbuddy.domain.like.batch

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class LikeLogDeleteJobScheduler (
    private val jobLauncher: JobLauncher,
    private val likeBatchLogDeleteJob: Job
) {

    @Scheduled(cron = "\${schedule.like-log-delete-job.cron}")
    @SchedulerLock(
        name = "LikeLogDeleteJobScheduler",
        lockAtMostFor = "\${schedule.like-log-delete-job.lockAtMostFor}",
        lockAtLeastFor = "\${schedule.like-log-delete-job.lockAtLeastFor}"
    )
    @Throws(Exception::class)
    fun runJob() {
        val params = JobParametersBuilder()
            .toJobParameters()
        jobLauncher.run(likeBatchLogDeleteJob, params)
    }
}
