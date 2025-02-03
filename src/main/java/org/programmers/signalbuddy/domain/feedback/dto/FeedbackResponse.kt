package org.programmers.signalbuddy.domain.feedback.dto

import org.programmers.signalbuddy.domain.feedback.entity.enums.AnswerStatus
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import java.time.LocalDateTime

data class FeedbackResponse(
    val feedbackId: Long,
    val subject: String,
    val content: String,
    val likeCount: Long,
    val answerStatus: AnswerStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val member: MemberResponse
)
