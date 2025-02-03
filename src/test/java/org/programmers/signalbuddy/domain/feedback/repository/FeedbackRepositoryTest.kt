package org.programmers.signalbuddy.domain.feedback.repository

import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.support.RepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.LocalDate

internal class FeedbackRepositoryTest : RepositoryTest() {
    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    private lateinit var member: Member

    @BeforeEach
    fun setup() {
        member = Member(
            email = "test@test.com", password = "123456", role = MemberRole.USER,
            nickname = "tester", memberStatus = MemberStatus.ACTIVITY,
            profileImageUrl = "https://test-image.com/test-123131"
        )
        member = memberRepository.save(member)

        val feedbackList: MutableList<Feedback> = ArrayList()
        for (i in 0..122) {
            val subject = "test subject"
            val content = "test content"
            val request = FeedbackWriteRequest(subject, content)
            feedbackList.add(Feedback.create(request.subject!!, request.content!!, member))
        }
        feedbackRepository.saveAll(feedbackList)
    }

    @Test
    fun `탈퇴하지 않은 유저들의 피드백 목록 가져오기`() {
        // when
        val pageable = PageRequest.of(3, 10)
        val answerStatus = -1L // 모든 피드백 보기
        val actual = feedbackRepository.findAllByActiveMembers(pageable, answerStatus)

        // then
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual.totalElements).isEqualTo(123)
            softAssertions.assertThat(actual.totalPages).isEqualTo(13)
            softAssertions.assertThat(actual.number).isEqualTo(3)
            softAssertions.assertThat(actual.content.size).isEqualTo(10)
            softAssertions.assertThat(actual.content[3]!!.feedbackId).isNotNull()
            softAssertions.assertThat(actual.content[3]!!.member.memberId)
                .isEqualTo(member.memberId)
        }
    }

    // TODO: 더 다양한 데이터를 이용하여 추가 검증 필요
    @Test
    fun `관리자, 피드백 목록을 다양한 조건으로 조회`() {
        // when
        val pageable: Pageable =
            PageRequest.of(3, 10, Sort.Direction.DESC, "feedbackId", "createdAt")
        val actual = feedbackRepository.findAll(
            pageable, null, null,
            0L
        )

        // then
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual.totalElements).isEqualTo(123)
            softAssertions.assertThat(actual.totalPages).isEqualTo(13)
            softAssertions.assertThat(actual.number).isEqualTo(3)
            softAssertions.assertThat(actual.content.size).isEqualTo(10)
            softAssertions.assertThat(actual.content[3]!!.feedbackId).isNotNull()
            softAssertions.assertThat(actual.content[3]!!.member.memberId)
                .isEqualTo(member.memberId)
        }
    }

    @Test
    fun `관리자, 피드백 목록을 다양한 조건으로 조회, 잘못된 컬럼명을 요청할 때`() {
        // given
        val pageable: Pageable = PageRequest.of(3, 10, Sort.Direction.DESC, "xxxxx")

        // when & then
        Assertions.assertThatThrownBy {
            feedbackRepository.findAll(
                pageable, null, LocalDate.now(),
                0L
            )
        }.isExactlyInstanceOf(InvalidDataAccessApiUsageException::class.java)
    }
}