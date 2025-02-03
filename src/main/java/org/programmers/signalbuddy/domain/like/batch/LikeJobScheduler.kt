package org.programmers.signalbuddy.domain.like.batch

import lombok.RequiredArgsConstructor
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class LikeJobScheduler (
    private val jobLauncher: JobLauncher,
    private val likeRequestJob: Job
) {

    @Scheduled(cron = "\${schedule.like-job.cron}")
    @SchedulerLock(
        name = "LikeJobScheduler",
        lockAtMostFor = "\${schedule.like-job.lockAtMostFor}",
        lockAtLeastFor = "\${schedule.like-job.lockAtLeastFor}"
    )
    @Throws(Exception::class)
    fun runJob() {
        val params = JobParametersBuilder()
            .toJobParameters()
        jobLauncher.run(likeRequestJob, params)
    }
}
