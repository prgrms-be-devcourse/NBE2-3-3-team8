package org.programmers.signalbuddy.domain.feedback.service

import lombok.RequiredArgsConstructor
import org.programmers.signalbuddy.domain.comment.repository.CommentRepository
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackMapper
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackResponse
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.Feedback.Companion.create
import org.programmers.signalbuddy.domain.feedback.exception.FeedbackErrorCode
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackJdbcRepository
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.feedback.repository.findByIdOrThrow
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.domain.member.repository.findByIdOrThrow
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.dto.PageResponse
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class FeedbackService (
    private val feedbackRepository: FeedbackRepository,
    private val memberRepository: MemberRepository,
    private val feedbackJdbcRepository: FeedbackJdbcRepository,
    private val commentRepository: CommentRepository
) {

    fun searchFeedbackList(
        pageable: Pageable,
        answerStatus: Long?
    ): PageResponse<FeedbackResponse?> {
        val responsePage =
            feedbackRepository.findAllByActiveMembers(pageable, answerStatus)
        return PageResponse(responsePage)
    }

    fun searchFeedbackList(
        pageable: Pageable,
        startDate: LocalDate?, endDate: LocalDate?, answerStatus: Long
    ): PageResponse<FeedbackResponse?> {
        val responsePage = feedbackRepository.findAll(
            pageable, startDate,
            endDate, answerStatus
        )
        return PageResponse(responsePage)
    }

    fun searchByKeyword(
        pageable: Pageable, keyword: String?,
        answerStatus: Long
    ): PageResponse<FeedbackResponse> {
        val responsePage = feedbackJdbcRepository.fullTextSearch(
            pageable,
            keyword, answerStatus
        )
        return PageResponse(responsePage)
    }

    fun searchFeedbackDetail(feedbackId: Long): FeedbackResponse {
        val feedback = feedbackRepository.findByIdOrThrow(feedbackId)
        return FeedbackMapper.INSTANCE.toResponse(feedback)
    }

    @Transactional
    fun writeFeedback(request: FeedbackWriteRequest, user: CustomUser2Member?): FeedbackResponse {
        val member = memberRepository.findByIdOrThrow(user?.memberId!!)

        val feedback = create(request.subject!!, request.content!!, member)
        val savedFeedback = feedbackRepository.save(feedback)

        return FeedbackMapper.INSTANCE.toResponse(savedFeedback)
    }

    @Transactional
    fun updateFeedback(feedbackId: Long, request: FeedbackWriteRequest, user: CustomUser2Member?) {
        val feedback = feedbackRepository.findByIdOrThrow(feedbackId)

        // 피드백 작성자와 수정 요청자가 다른 경우
        if (Member.isNotSameMember(user, feedback.member)) {
            throw BusinessException(FeedbackErrorCode.FEEDBACK_MODIFIER_NOT_AUTHORIZED)
        }

        feedback.updateFeedback(request)
    }

    @Transactional
    fun deleteFeedback(feedbackId: Long, user: CustomUser2Member?) {
        val feedback = feedbackRepository.findByIdOrThrow(feedbackId)

        // 피드백 작성자와 삭제 요청자가 다른 경우
        if (Member.isNotSameMember(user, feedback.member)) {
            throw BusinessException(FeedbackErrorCode.FEEDBACK_ELIMINATOR_NOT_AUTHORIZED)
        }

        commentRepository.deleteAllByFeedbackId(feedbackId)
        feedbackRepository.deleteById(feedbackId)
    }

    fun findPagedFeedbacksByMember(memberId: Long, pageable: Pageable): Page<FeedbackResponse?> {
        return feedbackRepository.findPagedByMember(memberId, pageable)
    }
}
