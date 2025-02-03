package org.programmers.signalbuddy.domain.comment.repository

import org.programmers.signalbuddy.domain.comment.dto.CommentResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomCommentRepository {
    fun findAllByFeedbackIdAndActiveMembers(
        feedbackId: Long,
        pageable: Pageable
    ): Page<CommentResponse?>
}
