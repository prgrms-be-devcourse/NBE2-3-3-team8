package org.programmers.signalbuddy.global.exception

class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
