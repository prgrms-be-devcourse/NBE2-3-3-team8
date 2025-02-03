package org.programmers.signalbuddy.domain.comment.dto

import com.querydsl.core.annotations.QueryProjection
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import java.time.LocalDateTime

data class CommentResponse (
    val commentId: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val member: MemberResponse
)
