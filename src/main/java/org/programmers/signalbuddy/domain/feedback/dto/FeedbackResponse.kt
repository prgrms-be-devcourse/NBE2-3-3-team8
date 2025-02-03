package org.programmers.signalbuddy.domain.feedback.dto

import org.programmers.signalbuddy.domain.feedback.entity.enums.AnswerStatus
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import java.time.LocalDateTime

data class FeedbackResponse(
    val feedbackId: Long = 0L,
    val subject: String = "",
    val content: String = "",
    val likeCount: Long = 0L,
    val answerStatus: AnswerStatus = AnswerStatus.BEFORE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val member: MemberResponse = MemberResponse()
)
