package org.programmers.signalbuddy.domain.like.exception

import org.programmers.signalbuddy.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class LikeErrorCode(
    override val httpStatus: HttpStatus,
    override val code: Int,
    override val message: String,
) : ErrorCode {

    ALREADY_ADDED_LIKE(HttpStatus.CONFLICT, 50000, "이미 좋아요 추가되었습니다."),
    NOT_FOUND_LIKE(HttpStatus.NOT_FOUND, 50001, "좋아요 데이터가 존재하지 않습니다."),
    ILLEGAL_REQUEST_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, 50002, "잘못된 좋아요 요청 상태입니다.");
}
