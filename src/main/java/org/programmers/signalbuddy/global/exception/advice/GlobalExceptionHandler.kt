package org.programmers.signalbuddy.global.exception.advice

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.programmers.signalbuddy.global.exception.BusinessException
import org.programmers.signalbuddy.global.exception.GlobalErrorCode
import org.programmers.signalbuddy.global.exception.advice.dto.ErrorResponse
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties.Logging
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        logError(e)
        val error = e.errorCode
        return ResponseEntity.status(error.httpStatus).body(ErrorResponse(error))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        logError(e)
        val code = GlobalErrorCode.BAD_REQUEST.code
        val message = e.bindingResult.fieldErrors[0].defaultMessage
        val errorResponse = message?.let { ErrorResponse(code, it) }
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidException(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        logError(e)
        val code = GlobalErrorCode.BAD_REQUEST.code
        val message = e.message
        val errorResponse = message?.let { ErrorResponse(code, it) }
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException::class)
    fun handleInvalidDataAccessApiUsage(
        e: InvalidDataAccessApiUsageException
    ): ResponseEntity<ErrorResponse> {
        logError(e)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(GlobalErrorCode.BAD_REQUEST))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        logError(e)
        val errorResponse = ErrorResponse(GlobalErrorCode.SERVER_ERROR)
        return ResponseEntity.internalServerError().body(errorResponse)
    }

    private fun logError(e: Exception) {
        log.error { "Exception occurred: [${e.javaClass.simpleName}] - ${e.message}" }
    }
}
