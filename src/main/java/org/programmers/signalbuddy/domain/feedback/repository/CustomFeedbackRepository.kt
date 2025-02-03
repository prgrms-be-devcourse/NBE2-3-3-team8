package org.programmers.signalbuddy.domain.feedback.repository

import org.programmers.signalbuddy.domain.feedback.dto.FeedbackResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface CustomFeedbackRepository {
    fun findAllByActiveMembers(pageable: Pageable, answerStatus: Long?): Page<FeedbackResponse?>

    fun findPagedByMember(memberId: Long, pageable: Pageable): Page<FeedbackResponse?>

    fun findAll(
        pageable: Pageable,
        startDate: LocalDate?, endDate: LocalDate?,
        answerStatus: Long?
    ): Page<FeedbackResponse?>
}
