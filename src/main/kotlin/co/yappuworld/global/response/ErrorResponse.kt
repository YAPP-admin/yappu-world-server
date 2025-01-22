package co.yappuworld.global.response

import co.yappuworld.global.exception.Error
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "실패응답")
data class ErrorResponse(
    @Schema(description = "메세지")
    val message: String,
    @Schema(description = "에러코드")
    val errorCode: String?
) : Response() {

    @Schema(description = "요청의 성공 여부", example = "false")
    override val isSuccess: Boolean = false

    companion object {
        fun of(
            message: String,
            errorCode: String? = null
        ): ErrorResponse {
            return ErrorResponse(
                message,
                errorCode
            )
        }

        fun of(error: Error): ErrorResponse {
            return ErrorResponse(
                error.message,
                error.code
            )
        }
    }
}
