package org.programmers.signalbuddy.global.exception

import org.springframework.http.HttpStatus

enum class GlobalErrorCode(
    override val httpStatus: HttpStatus,
    override val code: Int,
    override val message: String
) : ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, 90000, "잘못된 요청입니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 90001, "알 수 없는 에러가 발생했습니다. 관리자에게 문의하세요.");
}
