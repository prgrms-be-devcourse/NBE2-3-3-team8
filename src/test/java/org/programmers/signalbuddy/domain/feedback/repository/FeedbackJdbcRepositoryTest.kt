package org.programmers.signalbuddy.domain.feedback.repository

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.feedback.entity.Feedback.Companion.create
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.support.JdbcTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate

internal class FeedbackJdbcRepositoryTest : JdbcTest() {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var feedbackJdbcRepository: FeedbackJdbcRepository

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
            feedbackList.add(create(request.subject!!, request.content!!, member))
        }
        feedbackRepository.saveAll(feedbackList)

        jdbcTemplate.execute("CREATE FULLTEXT INDEX IF NOT EXISTS idx_subject_content ON feedbacks (subject, content)")
    }

    @Test
    fun `Full Text Search를 이용한 검색 쿼리`() {
        // when
        val pageable: Pageable = PageRequest.of(3, 10)
        val actual = feedbackJdbcRepository.fullTextSearch(pageable, "test", 0L)

        // then
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual.totalElements).isEqualTo(123)
            softAssertions.assertThat(actual.totalPages).isEqualTo(13)
            softAssertions.assertThat(actual.number).isEqualTo(3)
            softAssertions.assertThat(actual.content.size).isEqualTo(10)
            softAssertions.assertThat(actual.content[3].feedbackId).isNotNull()
            softAssertions.assertThat(actual.content[3].member.memberId)
                .isEqualTo(member.memberId)
        }
    }
}