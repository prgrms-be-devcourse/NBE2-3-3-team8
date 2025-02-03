package org.programmers.signalbuddy.domain.comment.dto

import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import java.time.LocalDateTime

data class CommentResponse (
    val commentId: Long = 0L,
    val content: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val member: MemberResponse = MemberResponse()
)
