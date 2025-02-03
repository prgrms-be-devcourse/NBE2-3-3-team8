package org.programmers.signalbuddy.domain.feedback.service

import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.feedback.exception.FeedbackErrorCode
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.exception.BusinessException
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.programmers.signalbuddy.global.support.ServiceTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@Transactional
internal class FeedbackServiceTest : ServiceTest() {
    @Autowired
    private lateinit var feedbackService: FeedbackService

    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

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
            Feedback.create(request.subject!!, request.content!!, member)
        )
    }

    @Test
    fun `피드백 작성 성공`() {
        // given
        val subject = "test subject"
        val content = "test content"
        val request = FeedbackWriteRequest(subject, content)
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        val actual = feedbackService.writeFeedback(request, user)

        // then
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual.feedbackId).isNotNull()
            softAssertions.assertThat(actual.subject).isEqualTo(subject)
            softAssertions.assertThat(actual.content).isEqualTo(content)
            softAssertions.assertThat(actual.likeCount).isZero()
            softAssertions.assertThat(actual.member.memberId)
                .isEqualTo(user.memberId)
        }
    }

    @Test
    fun `피드백 수정 성공`() {
        // given
        val feedbackId = feedback.feedbackId
        val updatedSubject = "test updated subject"
        val updatedContent = "test updated content"
        val request = FeedbackWriteRequest(updatedSubject, updatedContent)
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        feedbackService.updateFeedback(feedbackId!!, request, user)

        // then
        val actual = feedbackRepository.findById(feedbackId)
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual.get().feedbackId).isEqualTo(feedbackId)
            softAssertions.assertThat(actual.get().subject).isEqualTo(updatedSubject)
            softAssertions.assertThat(actual.get().content).isEqualTo(updatedContent)
            softAssertions.assertThat(actual.get().member.memberId)
                .isEqualTo(user.memberId)
        }
    }

    @Test
    fun `피드백 작성자와 다른 사람이 수정 시 실패`() {
        // given
        val feedbackId = feedback.feedbackId
        val updatedSubject = "test updated subject"
        val updatedContent = "test updated content"
        val request = FeedbackWriteRequest(updatedSubject, updatedContent)
        val user = CustomUser2Member(
            CustomUserDetails(
                9999999L, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when & then
        Assertions.assertThatThrownBy {
            feedbackService.updateFeedback(feedbackId!!, request, user)
        }.isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(FeedbackErrorCode.FEEDBACK_MODIFIER_NOT_AUTHORIZED.message)
    }

    @Test
    fun `피드백 삭제 성공`() {
        // given
        val feedbackId = feedback.feedbackId
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        feedbackService.deleteFeedback(feedbackId!!, user)

        // then
        Assertions.assertThat(feedbackRepository.existsById(feedbackId)).isFalse()
    }

    @Test
    fun `피드백 작성자와 다른 사람이 삭제 시 실패`() {
        // given
        val feedbackId = feedback.feedbackId
        val user = CustomUser2Member(
            CustomUserDetails(
                999999L, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when & then
        Assertions.assertThatThrownBy {
            feedbackService.deleteFeedback(feedbackId!!, user)
        }.isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(FeedbackErrorCode.FEEDBACK_MODIFIER_NOT_AUTHORIZED.message)
    }

    @Test
    fun `피드백 상세 조회 성공`() {
        // given
        val feedbackId = feedback.feedbackId

        // when
        val response = feedbackService.searchFeedbackDetail(feedbackId!!)

        // then
        val actual = feedbackRepository.findById(feedbackId)
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual.get().feedbackId).isEqualTo(feedbackId)
            softAssertions.assertThat(actual.get().subject).isEqualTo(response.subject)
            softAssertions.assertThat(actual.get().content).isEqualTo(response.content)
            softAssertions.assertThat(actual.get().member.memberId)
                .isEqualTo(feedback.member.memberId)
        }
    }

    @Test
    fun `Member id로 피드백 목록 조회`() {
        for (i in 1..10) {
            val subject = "test subject $i"
            val content = "test content $i"
            val request = FeedbackWriteRequest(subject, content)
            feedbackRepository.save(
                Feedback.create(request.subject!!, request.content!!, member)
            )
        }

        val feedbacks = feedbackService.findPagedFeedbacksByMember(
            member.memberId!!, PageRequest.of(0, 5)
        )

        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(feedbacks.totalPages).isEqualTo(3)
            softAssertions.assertThat(feedbacks.totalElements).isEqualTo(11)
            softAssertions.assertThat(feedbacks.content.size).isEqualTo(5) // 한 페이지당 5개

            val firstFeedback = feedbacks.content[0]
            softAssertions.assertThat(firstFeedback!!.subject).isEqualTo("test subject 10")
            softAssertions.assertThat(firstFeedback.content).isEqualTo("test content 10")
        }
    }
}