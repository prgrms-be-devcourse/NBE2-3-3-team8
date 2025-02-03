package org.programmers.signalbuddy.domain.comment.service

import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.programmers.signalbuddy.domain.comment.dto.CommentRequest
import org.programmers.signalbuddy.domain.comment.entity.Comment
import org.programmers.signalbuddy.domain.comment.exception.CommentErrorCode
import org.programmers.signalbuddy.domain.comment.repository.CommentRepository
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.feedback.entity.enums.AnswerStatus
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
import org.springframework.transaction.annotation.Transactional

@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class CommentServiceTest : ServiceTest() {
    @Autowired
    private lateinit var commentService: CommentService

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

    private lateinit var member: Member
    private lateinit var admin: Member
    private lateinit var feedback: Feedback
    private lateinit var comment: Comment

    @BeforeEach
    fun setup() {
        member = Member(
            email = "test@test.com", password = "123456", role = MemberRole.USER,
            nickname = "tester", memberStatus = MemberStatus.ACTIVITY,
            profileImageUrl = "https://test-image.com/test-123131"
        )
        member = memberRepository.save(member)

        admin = Member(
            email = "admin@test.com", password = "123456", role = MemberRole.ADMIN,
            nickname = "admin", memberStatus = MemberStatus.ACTIVITY,
            profileImageUrl = "https://test-image.com/test-123131"
        )
        admin = memberRepository.save(admin)

        val subject = "test subject"
        val content = "test content"
        val request = FeedbackWriteRequest(subject, content)
        feedback = feedbackRepository.save(
            Feedback.create(request.subject!!, request.content!!, member)
        )

        comment = Comment.create("test comment content", feedback, member)
        comment = commentRepository.save(comment)
    }

    @Test
    @Order(1)
    fun `댓글 작성 성공`() {
        // given
        val feedbackId = feedback.feedbackId
        val content = "test comment content"
        val request = CommentRequest(feedbackId, content)
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        commentService.writeComment(request, user)

        // then
        val actual = commentRepository.findById(2L)
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual).get().isNotNull()
            softAssertions.assertThat(actual.get().commentId).isNotNull()
            softAssertions.assertThat(actual.get().content).isEqualTo(content)
            softAssertions.assertThat(actual.get().member.memberId)
                .isEqualTo(user.memberId)
            softAssertions.assertThat(actual.get().feedback.feedbackId)
                .isEqualTo(feedbackId)
        }
    }

    @Test
    @Order(2)
    fun `관리자 댓글 작성 성공`() {
        // given
        val feedbackId = feedback.feedbackId
        val content = "test admin comment content"
        val request = CommentRequest(feedbackId, content)
        val user = CustomUser2Member(
            CustomUserDetails(
                admin.memberId, "", "",
                "", "", MemberRole.ADMIN, MemberStatus.ACTIVITY
            )
        )

        // when
        commentService.writeComment(request, user)

        // then
        val actual = commentRepository.findById(2L)
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual).get().isNotNull()
            softAssertions.assertThat(actual.get().commentId).isNotNull()
            softAssertions.assertThat(actual.get().content).isEqualTo(content)
            softAssertions.assertThat(actual.get().member.memberId)
                .isEqualTo(user.memberId)
            softAssertions.assertThat(actual.get().feedback.feedbackId)
                .isEqualTo(feedbackId)
            softAssertions.assertThat(actual.get().feedback.answerStatus)
                .isEqualTo(AnswerStatus.COMPLETION)
        }
    }

    @Test
    @Order(3)
    fun `댓글 수정 성공`() {
        // given
        val feedbackId = feedback.feedbackId
        val updatedContent = "update comment content"
        val request = CommentRequest(feedbackId, updatedContent)
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when
        commentService.updateComment(comment.commentId!!, request, user)

        // then
        val actual = comment.commentId?.let { commentRepository.findById(it) }
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(actual?.get()?.commentId).isNotNull()
            softAssertions.assertThat(actual?.get()?.content).isEqualTo(updatedContent)
            softAssertions.assertThat(actual?.get()?.member?.memberId)
                .isEqualTo(user.memberId)
        }
    }

    @Test
    @Order(4)
    fun `댓글 작성자와 다른 사람이 수정 시, 실패`() {
        // given
        val feedbackId = feedback.feedbackId
        val updatedContent = "update comment content"
        val request = CommentRequest(feedbackId, updatedContent)
        val user = CustomUser2Member(
            CustomUserDetails(
                999999L, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when & then
        Assertions.assertThatThrownBy {
            commentService.updateComment(comment.commentId!!, request, user)
        }.isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(CommentErrorCode.COMMENT_MODIFIER_NOT_AUTHORIZED.message)
    }

    @Test
    @Order(5)
    fun `댓글 삭제 성공`() {
        // given
        val user = CustomUser2Member(
            CustomUserDetails(
                member.memberId, "", "",
                "", "", MemberRole.ADMIN, MemberStatus.ACTIVITY
            )
        )

        // when
        commentService.deleteComment(comment.commentId!!, user)

        // then
        Assertions.assertThat(comment.commentId?.let { commentRepository.existsById(it) }).isFalse()
    }

    @Test
    @Order(6)
    fun `댓글 작성자와 다른 사람이 삭제 시, 실패`() {
        // given
        val user = CustomUser2Member(
            CustomUserDetails(
                999999L, "", "",
                "", "", MemberRole.USER, MemberStatus.ACTIVITY
            )
        )

        // when & then
        Assertions.assertThatThrownBy {
            commentService.deleteComment(
                comment.commentId!!, user
            )
        }.isExactlyInstanceOf(BusinessException::class.java)
            .hasMessage(CommentErrorCode.COMMENT_ELIMINATOR_NOT_AUTHORIZED.message)
    }

    @Test
    @Order(7)
    fun `관리자 댓글 삭제 성공`() {
        // given
        val feedbackId = feedback.feedbackId
        val content = "test admin comment content"
        val request = CommentRequest(feedbackId, content)
        val user = CustomUser2Member(
            CustomUserDetails(
                admin.memberId, "", "",
                "", "", MemberRole.ADMIN, MemberStatus.ACTIVITY
            )
        )

        // when
        commentService.writeComment(request, user)
        commentService.deleteComment(2L, user)

        // then
        SoftAssertions.assertSoftly { softAssertions: SoftAssertions ->
            softAssertions.assertThat(commentRepository.existsById(10L)).isFalse()
            softAssertions.assertThat(
                feedback.feedbackId?.let { feedbackRepository.findById(it).get().answerStatus }
            ).isEqualTo(AnswerStatus.BEFORE)
        }
    }
}