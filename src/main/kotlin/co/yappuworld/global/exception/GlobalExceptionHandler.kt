package co.yappuworld.global.exception

import co.yappuworld.global.response.ErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
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

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        logger.error { e.message }

        return e.bindingResult.fieldErrors[0].defaultMessage?.let {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    ErrorResponse.of(
                        message = it,
                        errorCode = GlobalError.INVALID_REQUEST_ARGUMENT.code
                    )
                )
        } ?: getInternalServerErrorResponse()
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error { e.message }
        return getInternalServerErrorResponse()
    }

    private fun getHttpStatusBy(errorType: ErrorType): HttpStatus {
        return when (errorType) {
            ErrorType.WRONG_ARGUMENT -> HttpStatus.BAD_REQUEST
            ErrorType.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            ErrorType.NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorType.WRONG_STATE -> HttpStatus.CONFLICT
            ErrorType.UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }

    private fun getInternalServerErrorResponse(): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.of(GlobalError.INTERNAL_SERVER_ERROR))
    }
}
