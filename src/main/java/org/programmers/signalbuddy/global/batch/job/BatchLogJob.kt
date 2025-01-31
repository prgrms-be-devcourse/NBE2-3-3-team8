package org.programmers.signalbuddy.global.batch.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchLogJob (private val jdbcTemplate: JdbcTemplate) {

    @Bean
    fun oldBatchLogDeleteJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Job {
        return JobBuilder("oldBatchLogDeleteJob", jobRepository)
            .incrementer(RunIdIncrementer()) // 동일한 파라미터를 다시 실행
            .start(deleteOldLog(jobRepository, transactionManager))
            .build()
    }

    @Bean
    @JobScope
    fun deleteOldLog(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("deleteOldLog", jobRepository)
            .tasklet(OldLogDeleteTasklet(jdbcTemplate), transactionManager)
            .allowStartIfComplete(true) // COMPLETED 되어도 재실행에 포함시키기
            .build()
    }
}
