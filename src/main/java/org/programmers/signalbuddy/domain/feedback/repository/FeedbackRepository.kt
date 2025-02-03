package org.programmers.signalbuddy.domain.feedback.repository

import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.feedback.exception.FeedbackErrorCode
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
interface FeedbackRepository: JpaRepository<Feedback, Long>, CustomFeedbackRepository {
    fun findByIdOrThrow(feedbackId: Long): Feedback = findByIdOrNull(feedbackId)
        ?: throw BusinessException(FeedbackErrorCode.NOT_FOUND_FEEDBACK)
}
