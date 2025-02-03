package org.programmers.signalbuddy.domain.like.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.feedback.entity.Feedback.Companion.create
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.like.entity.Like
import org.programmers.signalbuddy.domain.like.repository.LikeRepository
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.db.RedisTestContainer
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.programmers.signalbuddy.global.support.ServiceTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate

internal class LikeServiceTest : ServiceTest(), RedisTestContainer {
    @Autowired
    private lateinit var likeService: LikeService

    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var likeRepository: LikeRepository

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    private lateinit var member: Member
    private lateinit var feedback: Feedback

    @BeforeEach
    fun setup() {
        member = Member(
            email = "test@test.com", password = "123456", role = MemberRole.USER,
            nickname = "tester", memberStatus = MemberStatus.ACTIVITY,
            profileImageUrl = "https://test-image.com/test-123131"
        )
        member = memberRepository.save(member)

        val subject = "test subject"
        val content = "test content"
        val request = FeedbackWriteRequest(subject, content)
        feedback = feedbackRepository.save(
            create(request.subject!!, request.content!!, member)
        )
    }

    @Test
    fun `좋아요 추가 성공`() {
        // given
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        likeService.addLike(feedback.feedbackId!!, user)

        // then
        val deleteLike =
            redisTemplate.opsForValue()[(LikeService.likeKeyPrefix + feedback.feedbackId) + ":" + member.memberId]
        Assertions.assertThat(deleteLike).isEqualTo("ADD")
        redisTemplate.delete(
            LikeService.likeKeyPrefix + feedback.feedbackId + ":" + member.memberId
        )
    }

    @Test
    fun `좋아요 취소 성공`() {
        // given
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        likeRepository.save(Like.create(member, feedback))
        likeService.deleteLike(feedback.feedbackId!!, user)

        // then
        val deleteLike =
            redisTemplate!!.opsForValue()[(LikeService.likeKeyPrefix + feedback.feedbackId) + ":" + member.memberId]
        Assertions.assertThat(deleteLike).isEqualTo("CANCEL")
        redisTemplate.delete(
            LikeService.likeKeyPrefix + feedback!!.feedbackId + ":" + member.memberId
        )
    }

    @Test
    fun `해당 좋아요가 존재할 때`() {
        // given
        val feedbackId = feedback.feedbackId
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        likeRepository.save(Like.create(member, feedback))
        val actual = likeService.existsLike(feedbackId!!, user)

        // then
        Assertions.assertThat(actual.status).isTrue()
    }

    @Test
    fun `해당 좋아요가 존재하지 않을 때`() {
        // given
        val feedbackId = feedback.feedbackId
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        val actual = likeService.existsLike(feedbackId!!, user)

        // then
        Assertions.assertThat(actual.status).isFalse()
    }
}