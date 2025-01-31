package org.programmers.signalbuddy.global.exception

import org.springframework.http.HttpStatus

interface ErrorCode {

    val httpStatus: HttpStatus
    val code: Int
    val message: String
}
