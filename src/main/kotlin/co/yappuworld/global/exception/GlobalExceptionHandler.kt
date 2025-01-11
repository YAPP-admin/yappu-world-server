package co.yappuworld.global.exception

import co.yappuworld.global.response.ErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger { }

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        logger.error { e.message }
        return ResponseEntity
            .status(getHttpStatusBy(e.error.type))
            .body(ErrorResponse.of(e.error))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error { e.message }
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.of("서버 관리자에게 문의해주세요.", null))
    }

    private fun getHttpStatusBy(errorType: ErrorType): HttpStatus {
        return when (errorType) {
            ErrorType.WRONG_ARGUMENT -> HttpStatus.BAD_REQUEST
            ErrorType.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            ErrorType.NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.WRONG_STATE -> HttpStatus.CONFLICT
        }
    }
}
