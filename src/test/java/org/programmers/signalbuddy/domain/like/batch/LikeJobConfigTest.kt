package org.programmers.signalbuddy.domain.like.batch

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.feedback.entity.Feedback.Companion.create
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.feedback.repository.findByIdOrThrow
import org.programmers.signalbuddy.domain.like.service.LikeService
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.db.RedisTestContainer
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.programmers.signalbuddy.global.support.BatchTest
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

internal class LikeJobConfigTest : BatchTest(), RedisTestContainer {
    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var likeRequestJob: Job

    @Autowired
    private lateinit var likeService: LikeService

    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    private lateinit var savedMemberList: List<Member>

    @BeforeEach
    fun setup() {
        val memberList: MutableList<Member> = ArrayList()
        for (i in 0..99) {
            val member = Member(
                email = "test@test.com", password = "123456$i", role = MemberRole.USER,
                nickname = "tester", memberStatus = MemberStatus.ACTIVITY,
                profileImageUrl = "https://test-image.com/test-123131"
            )
            memberList.add(member)
        }
        savedMemberList = memberRepository.saveAll(memberList)
    }

    @Test
    @Throws(Exception::class)
    fun `동시에 많은 좋아요 추가,취소 요청이 발생할 때, 좋아요 개수의 정합성 확인`() {
        // given
        val addLikeThreadCount = savedMemberList.size // 좋아요 추가 요청의 스레드 개수
        val executorService = Executors.newFixedThreadPool(addLikeThreadCount) // 스레드 풀 생성
        val latch = CountDownLatch(addLikeThreadCount) // 스레드 대기 관리
        val deleteLikeThreadCount = (addLikeThreadCount * 0.3).toInt() // 좋아요 취소 요청의 스레드 개수
        val latch2 = CountDownLatch(deleteLikeThreadCount) // 스레드 대기 관리
        val executorService2 = Executors.newFixedThreadPool(deleteLikeThreadCount) // 스레드 풀 생성

        val subject = "test subject"
        val content = "test content"
        val request = FeedbackWriteRequest(subject, content)
        val savedFeedback = feedbackRepository.saveAndFlush(
            Feedback.create(request.subject!!, request.content!!, savedMemberList[0])
        )

        // when
        // 좋아요 추가
        for (i in 0 until addLikeThreadCount) {
            val member = savedMemberList[i]
            executorService.execute {
                try {
                    val user = CustomUser2Member(
                        CustomUserDetails(
                            member.memberId, "", "",
                            "", "", MemberRole.USER, MemberStatus.ACTIVITY
                        )
                    )

                    likeService.addLike(savedFeedback.feedbackId!!, user)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    latch.countDown()
                }
            }
        }

        // 스레드가 다 끝날 때까지 기다리기
        latch.await()
        executorService.shutdown()

        // 좋아요 취소
        for (i in 0 until deleteLikeThreadCount) {
            val member = savedMemberList[i]
            executorService2.execute {
                try {
                    val user = CustomUser2Member(
                        CustomUserDetails(
                            member.memberId, "", "",
                            "", "", MemberRole.USER, MemberStatus.ACTIVITY
                        )
                    )

                    likeService.deleteLike(savedFeedback.feedbackId!!, user)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    latch2.countDown()
                }
            }
        }

        // 스레드가 다 끝날 때까지 기다리기
        latch2.await()
        executorService2.shutdown()

        jobLauncherTestUtils.job = likeRequestJob
        val jobExecution = jobLauncherTestUtils.launchJob()

        // then
        val updatedFeedback = feedbackRepository.findByIdOrThrow(savedFeedback.feedbackId!!)
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(jobExecution.exitStatus)
                .isEqualTo(ExitStatus.COMPLETED)
            softAssertions.assertThat(updatedFeedback.likeCount)
                .isEqualTo((addLikeThreadCount - deleteLikeThreadCount).toLong())
        }
    }
}