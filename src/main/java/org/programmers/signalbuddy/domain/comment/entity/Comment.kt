package org.programmers.signalbuddy.domain.comment.entity

import jakarta.persistence.*
import org.programmers.signalbuddy.domain.basetime.BaseTimeEntity
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.member.entity.Member

@Entity(name = "comments")
class Comment private constructor(
    content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    val feedback: Feedback,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentId: Long? = null

    @Column(nullable = false)
    var content: String = content
        protected set

    companion object {
        fun create(content: String, feedback: Feedback, member: Member) =
            Comment(content, feedback, member)
    }

    fun updateContent(content: String) {
        this.content = content
    }
}