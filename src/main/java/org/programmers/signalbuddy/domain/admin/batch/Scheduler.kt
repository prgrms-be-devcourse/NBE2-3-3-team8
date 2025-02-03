package org.programmers.signalbuddy.domain.admin.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class Scheduler(private val jobLauncher: JobLauncher,
                private val deleteMemberJob: Job) {
    @Scheduled(cron = "0 59 23 * * ?")
    fun runJob() {
        val params = JobParametersBuilder().toJobParameters()
        jobLauncher.run(deleteMemberJob, params)
    }
}