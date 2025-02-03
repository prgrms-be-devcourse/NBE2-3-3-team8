package org.programmers.signalbuddy.domain.like.entity

import jakarta.persistence.*
import org.programmers.signalbuddy.domain.basetime.BaseTimeEntity
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.member.entity.Member

@Entity(name = "likes")
class Like private constructor(
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val member: Member,

    @JoinColumn(name = "feedback_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val feedback: Feedback
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val likeId: Long? = null

    companion object {
        fun create(member: Member, feedback: Feedback) = Like(member, feedback)
    }
}

