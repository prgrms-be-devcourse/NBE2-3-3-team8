package org.programmers.signalbuddy.domain.comment.repository

import org.programmers.signalbuddy.domain.comment.entity.Comment
import org.programmers.signalbuddy.domain.comment.exception.CommentErrorCode
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

fun CommentRepository.findByIdOrThrow(commentId: Long) = findByIdOrNull(commentId)
    ?: throw BusinessException(CommentErrorCode.NOT_FOUND_COMMENT)

@Repository
interface CommentRepository : JpaRepository<Comment, Long>, CustomCommentRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM comments c WHERE c.feedback.feedbackId = :feedbackId")
    fun deleteAllByFeedbackId(@Param("feedbackId") feedbackId: Long)
}
