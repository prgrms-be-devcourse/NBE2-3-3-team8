package org.programmers.signalbuddy.domain.like.batch

import org.programmers.signalbuddy.global.batch.dto.BatchExecutionId
import org.programmers.signalbuddy.global.batch.repository.BatchJdbcRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime
import java.util.Map
import javax.sql.DataSource

@Configuration
class LikeBatchLogJobConfig (
    private val dataSource: DataSource,
    private val batchJdbcRepository: BatchJdbcRepository,

    @Value("\${schedule.like-log-delete-job.expired-minutes}")
    private val expiredMinutes: String
) {

    companion object {
        private const val CHUNK_SIZE = 100
    }

    @Bean
    fun likeBatchLogDeleteJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Job {
        return JobBuilder("likeBatchLogDeleteJob", jobRepository)
            .incrementer(RunIdIncrementer()) // 동일한 파라미터를 다시 실행
            .start(deleteLikeLogBatch(jobRepository, transactionManager))
            .build()
    }

    @Bean
    @JobScope
    fun deleteLikeLogBatch(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("deleteLikeLogBatch", jobRepository)
            .chunk<BatchExecutionId, BatchExecutionId>(CHUNK_SIZE, transactionManager)
            .reader(executionPagingReader())
            .writer(deleteLog())
            .allowStartIfComplete(true) // COMPLETED 되어도 재실행에 포함시키기
            .build()
    }

    @Bean
    @StepScope
    fun executionPagingReader(): JdbcPagingItemReader<BatchExecutionId> {
        return JdbcPagingItemReaderBuilder<BatchExecutionId>()
            .name("executionPagingReader")
            .dataSource(dataSource)
            .selectClause("SELECT STEP_EXECUTION_ID, JOB_EXECUTION_ID")
            .fromClause("FROM BATCH_STEP_EXECUTION")
            .whereClause(
                "WHERE STEP_NAME IN ('updateLikeBatch', 'deleteLikeLogBatch')"
                        + "AND START_TIME < :threshold"
            )
            .parameterValues(
                Map.of<String, Any>(
                    "threshold",
                    LocalDateTime.now().minusMinutes(expiredMinutes.toLong())
                )
            )
            .sortKeys(Map.of("STEP_EXECUTION_ID", Order.ASCENDING))
            .pageSize(CHUNK_SIZE)
            .rowMapper(BeanPropertyRowMapper(BatchExecutionId::class.java))
            .build()
    }

    @Bean
    @StepScope
    fun deleteLog(): ItemWriter<BatchExecutionId> {
        return ItemWriter { chunk: Chunk<out BatchExecutionId> ->
            val executionIds = chunk.items as List<BatchExecutionId>
            batchJdbcRepository.deleteAllByStepExecutionIdInBatch(
                "BATCH_STEP_EXECUTION_CONTEXT", executionIds
            )
            batchJdbcRepository.deleteAllByStepExecutionIdInBatch(
                "BATCH_STEP_EXECUTION", executionIds
            )
            batchJdbcRepository.deleteAllByJobExecutionIdInBatch(
                "BATCH_JOB_EXECUTION_CONTEXT", executionIds
            )
            batchJdbcRepository.deleteAllByJobExecutionIdInBatch(
                "BATCH_JOB_EXECUTION", executionIds
            )
        }
    }
}
