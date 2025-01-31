package org.programmers.signalbuddy.global.exception.advice.dto

import org.programmers.signalbuddy.global.exception.ErrorCode

class ErrorResponse (
    val code: Int,
    val message: String
) {
    constructor(errorCode: ErrorCode) : this(errorCode.code, errorCode.message)
}
