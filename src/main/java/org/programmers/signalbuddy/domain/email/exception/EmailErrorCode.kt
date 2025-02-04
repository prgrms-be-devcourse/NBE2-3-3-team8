package org.programmers.signalbuddy.domain.email.exception

import org.programmers.signalbuddy.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class EmailErrorCode(
    override val httpStatus: HttpStatus,
    override val code: Int,
    override val message: String
) : ErrorCode {

    NOT_MATCH_AUTHCODE(HttpStatus.BAD_REQUEST, 30000, "인증 코드가 일치하지 않습니다.");
}