package org.programmers.signalbuddy.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus

data class MemberResponse(
    @Schema(description = "회원 ID", example = "1")
    val memberId: Long,

    @Schema(description = "회원 이메일", example = "test@example.com")
    val email: String,

    @Schema(description = "회원 닉네임", example = "TestUser")
    val nickname: String,

    @Schema(description = "프로필 이미지 URL", example = "http://example.com/profile.jpg")
    val profileImageUrl: String,

    @Schema(description = "회원 역할", example = "USER")
    val role: MemberRole,

    @Schema(description = "회원 상태", exampleClasses = [MemberStatus::class])
    val memberStatus: MemberStatus
)
