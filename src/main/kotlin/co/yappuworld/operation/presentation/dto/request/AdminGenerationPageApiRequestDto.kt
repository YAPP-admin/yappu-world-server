package co.yappuworld.operation.presentation.dto.request

import co.yappuworld.operation.application.dto.request.AdminGenerationPageAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class AdminGenerationPageApiRequestDto(
    @field:Schema(description = "페이지 번호", required = true)
    @field:Min(value = 1L, message = "페이지 번호는 1 이상이어야 합니다.")
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 개수", required = true)
    @field:Min(value = 1L, message = "데이터는 1개 이상이어야 합니다.")
    val size: Int
) {

    fun toAppRequest(): AdminGenerationPageAppRequestDto =
        AdminGenerationPageAppRequestDto(
            page = page,
            limit = size
        )
}
