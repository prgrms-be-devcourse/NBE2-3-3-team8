package org.programmers.signalbuddy.domain.social.entity

import jakarta.persistence.*
import org.programmers.signalbuddy.domain.basetime.BaseTimeEntity
import org.programmers.signalbuddy.domain.member.entity.Member

@Entity
class SocialProvider(
    @Column(nullable = false) var socialId: String,
    @Column(nullable = false) var oauthProvider: String,
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false) val member: Member? = null
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val socialProviderId: Long? = null
}






