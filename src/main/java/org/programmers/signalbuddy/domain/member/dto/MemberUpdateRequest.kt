package org.programmers.signalbuddy.domain.member.dto


import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.Email
import org.springframework.web.multipart.MultipartFile

data class MemberUpdateRequest(

    @field:Email(message = "이메일이 유효하지 않습니다.") @Schema(
        description = "이메일",
        requiredMode = RequiredMode.NOT_REQUIRED,
        defaultValue = "update@example.com"
    ) val email: String? = null,

    @Schema(
        description = "비밀번호", requiredMode = RequiredMode.NOT_REQUIRED, defaultValue = "password123"
    ) val password: String? = null,

    @Schema(
        description = "닉네임", requiredMode = RequiredMode.NOT_REQUIRED, defaultValue = "Nickname"
    ) val nickname: String? = null,

    @Schema(
        description = "프로필 사진 파일",
        requiredMode = RequiredMode.NOT_REQUIRED,
        defaultValue = "profile-img.png"
    ) val imageFile: MultipartFile? = null
)