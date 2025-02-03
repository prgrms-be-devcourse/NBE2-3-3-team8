package org.programmers.signalbuddy.domain.feedback.entity

import jakarta.persistence.*
import org.programmers.signalbuddy.domain.basetime.BaseTimeEntity
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackWriteRequest
import org.programmers.signalbuddy.domain.feedback.entity.enums.AnswerStatus
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.global.exception.BusinessException
import org.programmers.signalbuddy.global.exception.GlobalErrorCode

@Entity(name = "feedbacks")
class Feedback private constructor(
    subject: String,
    content: String,
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member
): BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val feedbackId: Long? = null

    @Column(nullable = false)
    var subject: String = subject
        protected set

    @Column(nullable = false)
    var content: String = content
        protected set

    @Column(nullable = false)
    var likeCount: Long = 0L
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var answerStatus: AnswerStatus = AnswerStatus.BEFORE
        protected set

    companion object {
        @JvmStatic
        fun create(subject: String, content: String, member: Member): Feedback {
            return Feedback(subject, content, member)
        }
    }

    fun updateFeedback(request: FeedbackWriteRequest) {
        if (this.subject != request.subject) {
            this.subject = request.subject!!
        }
        if (this.content != request.content) {
            this.content = request.content!!
        }
    }

    fun updateFeedbackStatus() = when {
        AnswerStatus.BEFORE == this.answerStatus -> {
            this.answerStatus = AnswerStatus.COMPLETION
        }

        AnswerStatus.COMPLETION == this.answerStatus -> {
            this.answerStatus = AnswerStatus.BEFORE
        }

        else -> {
            throw BusinessException(GlobalErrorCode.BAD_REQUEST)
        }
    }

    fun increaseLike() {
        this.likeCount += 1
    }

    fun decreaseLike() {
        if (this.likeCount > 0) {
            this.likeCount -= 1
        }
    }
}