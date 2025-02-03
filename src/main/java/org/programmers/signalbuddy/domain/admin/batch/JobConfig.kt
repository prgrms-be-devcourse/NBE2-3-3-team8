package org.programmers.signalbuddy.domain.admin.batch

import jakarta.persistence.EntityManagerFactory
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class JobConfig(
    private val memberRepository: MemberRepository,
    private val entityManagerFactory: EntityManagerFactory
) {
    private val chunkSize: Int = 10
    private val log: Logger = LoggerFactory.getLogger(JobConfig::class.java)

    @Bean
    fun deleteMemberJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Job {
        return JobBuilder("deleteMemberJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(deleteMemberStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    @JobScope
    fun deleteMemberStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("deleteMemberStep", jobRepository)
            .chunk<Member, Member>(chunkSize, transactionManager)
            .allowStartIfComplete(true) // COMPLETED 되어도 재실행
            .reader(customReader())
            .writer(deleteMemberWriter())
            .build()
    }

    @Bean
    @StepScope
    fun customReader(): CustomReader {
        return CustomReader(entityManagerFactory, chunkSize)
    }

    @Bean
    @StepScope
    fun deleteMemberWriter(): ItemWriter<Member> {
        return ItemWriter { items: Chunk<out Member>? ->
            if (items == null || items.isEmpty) {
                return@ItemWriter
            }

            items.forEach { member ->
                try {
                    memberRepository.deleteById(member.memberId)
                } catch (e: Exception) {
                    log.info(e.message);
                }
            }
        }
    }
}
