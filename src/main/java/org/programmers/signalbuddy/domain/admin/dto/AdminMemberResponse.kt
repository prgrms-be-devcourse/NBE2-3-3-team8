package org.programmers.signalbuddy.domain.admin.dto

import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import java.time.LocalDateTime

data class AdminMemberResponse(
    val memberId: Long?,
    val email: String? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val role: MemberRole? = null,
    val memberStatus: MemberStatus? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val userAddress: String? = null,
    val bookmarkCount: Int = 0
    //TODO : Bookmark List 추가
)
