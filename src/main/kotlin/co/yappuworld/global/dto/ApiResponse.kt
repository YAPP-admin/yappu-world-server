package co.yappuworld.global.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ApiResponse<T : Any?>(
    @Schema(description = "요청의 성공 여부")
    val isSuccess: Boolean,
    @Schema(description = "메세지")
    val message: String?,
    @Schema(description = "에러코드")
    val errorCode: String?,
    @Schema(description = "응답 본문")
    val body: T?
) {
    companion object {
        fun <T> success(body: T?): ApiResponse<T> {
            return ApiResponse(
                true,
                null,
                null,
                body
            )
        }

        fun <T> fail(
            message: String,
            errorCode: String,
            body: T?
        ): ApiResponse<T> {
            return ApiResponse(
                false,
                message,
                errorCode,
                body
            )
        }
    }
}
