package org.programmers.signalbuddy.domain.comment.repository

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.domain.comment.entity.Comment
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.support.RepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

internal class CommentRepositoryTest : RepositoryTest() {
    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

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

        val commentList = ArrayList<Comment>()
        for (i in 0..29) {
            val comment: Comment = Comment.create("test comment content", feedback, member)
            commentList.add(comment)
        }
        commentRepository.saveAll(commentList)
    }

    @Test
    fun `특정 피드백의 댓글 목록 가져오기`() {
        // given
        val feedbackId = feedback.feedbackId

        // when
        val pageable: Pageable = PageRequest.of(2, 7)
        val actual = commentRepository.findAllByFeedbackIdAndActiveMembers(
            feedbackId!!, pageable
        )

        // then
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual.totalElements).isEqualTo(30)
            softAssertions.assertThat(actual.totalPages).isEqualTo(5)
            softAssertions.assertThat(actual.number).isEqualTo(2)
            softAssertions.assertThat(actual.content.size).isEqualTo(7)
            softAssertions.assertThat(actual.content[3]!!.commentId).isNotNull()
            softAssertions.assertThat(actual.content[3]!!.content)
                .isEqualTo("test comment content")
            softAssertions.assertThat(actual.content[3]!!.member.memberId)
                .isEqualTo(member.memberId)
        }
    }
}