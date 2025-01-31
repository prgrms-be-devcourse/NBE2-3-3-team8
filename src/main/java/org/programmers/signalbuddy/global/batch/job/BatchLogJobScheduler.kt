package org.programmers.signalbuddy.global.batch.job

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class BatchLogJobScheduler (
    private val jobLauncher: JobLauncher,
    private val oldBatchLogDeleteJob: Job
) {

    @Scheduled(cron = "0 0 3 * * ?")
    @SchedulerLock(name = "BatchLogJobScheduler", lockAtMostFor = "23h", lockAtLeastFor = "23h")
    @Throws(Exception::class)
    fun runJob() {
        val params = JobParametersBuilder()
            .toJobParameters()
        jobLauncher.run(oldBatchLogDeleteJob, params)
    }
}
