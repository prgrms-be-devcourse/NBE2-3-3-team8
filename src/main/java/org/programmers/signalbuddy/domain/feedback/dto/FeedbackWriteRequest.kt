package org.programmers.signalbuddy.domain.feedback.dto

import jakarta.validation.constraints.NotBlank
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

data class FeedbackWriteRequest (
    @field:NotBlank(message = "피드백 제목은 비어있을 수 없습니다.")
    var subject: String? = null,

    @field:NotBlank(message = "피드백 내용은 비어있을 수 없습니다.")
    var content: String? = null
)
