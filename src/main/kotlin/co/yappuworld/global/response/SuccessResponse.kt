package co.yappuworld.global.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "성공 응답")
class SuccessResponse<T : Any?>(
    @Schema(description = "응답 본문")
    val data: T? = null
) : Response() {

    @Schema(description = "요청의 성공 여부", example = "true")
    override val isSuccess: Boolean = true

    companion object {
        fun <T> of(body: T?): SuccessResponse<T> {
            return SuccessResponse(body)
        }
    }
}
