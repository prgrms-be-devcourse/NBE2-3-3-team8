package org.programmers.signalbuddy.domain.like.batch

import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.like.dto.LikeUpdateRequest
import org.programmers.signalbuddy.domain.like.repository.LikeJdbcRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class LikeJobConfig (
    private val redisTemplate: StringRedisTemplate,
    private val feedbackRepository: FeedbackRepository,
    private val likeJdbcRepository: LikeJdbcRepository
) {

    @Bean
    fun likeRequestJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Job {
        return JobBuilder("likeRequestJob", jobRepository)
            .incrementer(RunIdIncrementer()) // 동일한 파라미터를 다시 실행
            .start(updateLikeBatch(jobRepository, transactionManager))
            .build()
    }

    @Bean
    @JobScope
    fun updateLikeBatch(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("updateLikeBatch", jobRepository)
            .chunk<LikeUpdateRequest, LikeUpdateRequest>(100, transactionManager)
            .reader(requestLikeReader())
            .writer(requestLikeWriter())
            .allowStartIfComplete(true) // COMPLETED 되어도 재실행에 포함시키기
            .build()
    }

    @Bean
    @StepScope
    fun requestLikeReader(): RequestLikeReader {
        return RequestLikeReader(redisTemplate)
    }

    @Bean
    @StepScope
    fun requestLikeWriter(): RequestLikeWriter {
        return RequestLikeWriter(feedbackRepository, likeJdbcRepository, redisTemplate)
    }
}
