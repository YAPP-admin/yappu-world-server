package co.yappuworld.global.response

import co.yappuworld.global.exception.Error
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "실패응답")
data class ErrorResponse(
    @Schema(description = "메세지")
    val message: String,
    @Schema(description = "에러 코드", nullable = true)
    val errorCode: String?,
    val errors: List<ErrorDetail> = emptyList()
) : Response() {

    @Schema(description = "요청의 성공 여부", example = "false")
    override val isSuccess: Boolean = false

    companion object {
        fun of(
            message: String,
            errorCode: String? = null,
            errors: List<ErrorDetail> = emptyList()
        ): ErrorResponse =
            ErrorResponse(
                message,
                errorCode,
                errors
            )

        fun of(error: Error): ErrorResponse =
            ErrorResponse(
                error.message,
                error.code
            )
    }
}

interface ErrorDetail

data class RequestFieldError(
    @Schema(description = "메세지")
    val message: String? = null,
    @Schema(description = "에러가 발생한 필드 이름")
    val field: String
) : ErrorDetail
