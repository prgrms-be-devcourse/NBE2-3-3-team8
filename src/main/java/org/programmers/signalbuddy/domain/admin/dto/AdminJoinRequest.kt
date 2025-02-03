package org.programmers.signalbuddy.domain.admin.dto

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class AdminJoinRequest (
    @Email(message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    val email:  String = "",

    @Schema( description = "비밀번호", requiredMode = RequiredMode.NOT_REQUIRED )
    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    val password:  String = ""
)