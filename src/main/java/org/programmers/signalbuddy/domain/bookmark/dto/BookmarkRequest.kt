package org.programmers.signalbuddy.domain.bookmark.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class BookmarkRequest(

    @field:Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
    @field:Max(value = 180, message = "경도는 180 이하여야 합니다.")
    @Schema(description = "경도", example = "126.9779451")
    val lng: Double,

    @field:Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
    @field:Max(value = 90, message = "위도는 90 이하여야 합니다.")
    @Schema(description = "위도", example = "37.5662952")
    val lat: Double,

    @field:NotBlank(message = "주소를 입력해주세요.")
    @Schema(description = "주소", example = "서울시청")
    val address: String,

    @Schema(description = "별명", example = "회사")
    val name: String? = null  // 기본값 null 허용 (선택 입력 가능)
)