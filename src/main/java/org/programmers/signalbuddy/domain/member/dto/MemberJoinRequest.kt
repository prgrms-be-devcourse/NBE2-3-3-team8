package org.programmers.signalbuddy.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.web.multipart.MultipartFile

data class MemberJoinRequest (
    @Schema(description = "이메일", requiredMode = RequiredMode.NOT_REQUIRED, defaultValue = "udpate@example.com")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    val email:  String = "",

    @Schema( description = "프로필 사진", requiredMode = RequiredMode.NOT_REQUIRED )
    val profileImageUrl: MultipartFile? = null,

    @Schema( description = "닉네임", requiredMode = RequiredMode.NOT_REQUIRED, defaultValue = "Nickname" )
    @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
    val nickname:  String = "",

    @Schema( description = "비밀번호", requiredMode = RequiredMode.NOT_REQUIRED )
    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    val password:  String = ""
)
