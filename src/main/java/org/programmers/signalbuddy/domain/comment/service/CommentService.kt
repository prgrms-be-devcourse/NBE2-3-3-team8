package org.programmers.signalbuddy.domain.comment.service

import org.programmers.signalbuddy.domain.comment.dto.CommentRequest
import org.programmers.signalbuddy.domain.comment.dto.CommentResponse
import org.programmers.signalbuddy.domain.comment.entity.Comment
import org.programmers.signalbuddy.domain.comment.exception.CommentErrorCode
import org.programmers.signalbuddy.domain.comment.repository.CommentRepository
import org.programmers.signalbuddy.domain.comment.repository.findByIdOrThrow
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.feedback.repository.findByIdOrThrow
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.domain.member.repository.findByIdOrThrow
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.dto.PageResponse
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService (
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
    private val feedbackRepository: FeedbackRepository
) {

    @Transactional
    fun writeComment(request: CommentRequest, user: CustomUser2Member?) {
        val member = memberRepository.findByIdOrThrow(user?.memberId!!)
        val feedback = feedbackRepository.findByIdOrThrow(request.feedbackId!!)

        val comment: Comment = Comment.create(request.content!!, feedback, member)

        // 관리자일 때 피드백 상태 변경
        if (Member.isAdmin(comment.member)) {
            feedback.updateFeedbackStatus()
        }

        commentRepository.save(comment)
    }

    fun searchCommentList(feedbackId: Long, pageable: Pageable): PageResponse<CommentResponse?> {
        val responsePage = commentRepository
            .findAllByFeedbackIdAndActiveMembers(feedbackId, pageable)
        return PageResponse(responsePage)
    }

    @Transactional
    fun updateComment(commentId: Long, request: CommentRequest, user: CustomUser2Member?) {
        val comment = commentRepository.findByIdOrThrow(commentId)

        // 수정 요청자와 댓글 작성자 다른 경우
        if (Member.isNotSameMember(user, comment.member)) {
            throw BusinessException(CommentErrorCode.COMMENT_MODIFIER_NOT_AUTHORIZED)
        }

        comment.updateContent(request.content!!)
    }

    @Transactional
    fun deleteComment(commentId: Long, user: CustomUser2Member?) {
        val comment = commentRepository.findByIdOrThrow(commentId)

        // 삭제 요청자와 댓글 작성자 다른 경우
        if (Member.isNotSameMember(user, comment.member)) {
            throw BusinessException(CommentErrorCode.COMMENT_ELIMINATOR_NOT_AUTHORIZED)
        }

        // 관리자일 때 피드백 상태 변경
        if (Member.isAdmin(comment.member)) {
            comment.feedback.updateFeedbackStatus()
        }

        commentRepository.deleteById(commentId)
    }
}
