package org.programmers.signalbuddy.domain.comment.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CommentRequest (
    @field:NotNull(message = "피드백 ID 값은 필수입니다.")
    val feedbackId: Long? = null,

    @field:NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
    val content: String? = null
)
