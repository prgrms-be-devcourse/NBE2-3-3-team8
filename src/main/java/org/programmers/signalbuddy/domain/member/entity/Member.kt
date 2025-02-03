package org.programmers.signalbuddy.domain.member.entity

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import jakarta.persistence.*
import org.programmers.signalbuddy.domain.basetime.BaseTimeEntity
import org.programmers.signalbuddy.domain.member.dto.MemberUpdateRequest
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.global.dto.CustomUser2Member

@Entity(name = "members")
class Member(
    @Column(nullable = false) var email: String,
    var password: String? = null,
    @Column(nullable = false) var nickname: String,
    var profileImageUrl: String? = null,
    @Column(nullable = false) @Enumerated(EnumType.STRING) val role: MemberRole,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var memberStatus: MemberStatus
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long? = null

    override fun toString() = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) =
        kotlinEquals(other = other, properties = equalsAndHashCodeProperties)

    override fun hashCode(): Int = kotlinHashCode(properties = equalsAndHashCodeProperties)

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Member::memberId)
        private val toStringProperties = arrayOf(
            Member::memberId,
            Member::email,
            Member::nickname,
            Member::profileImageUrl,
            Member::role,
            Member::memberStatus
        )

        // 관리자인지 확인
        fun isAdmin(member: Member): Boolean = member.role == MemberRole.ADMIN

        // 요청자와 작성자가 다른 경우
        fun isNotSameMember(user: CustomUser2Member?, member: Member): Boolean =
            user?.memberId != member.memberId!!
    }

    fun updateMember(
        request: MemberUpdateRequest, encodedPassword: String?, profileImageUrl: String?
    ) {
        request.email?.let { this.email = it }
        encodedPassword?.let { this.password = it }
        request.nickname?.let { this.nickname = it }
        profileImageUrl?.let { this.profileImageUrl = it }
    }

    fun softDelete() {
        this.memberStatus = MemberStatus.WITHDRAWAL
    }
}
