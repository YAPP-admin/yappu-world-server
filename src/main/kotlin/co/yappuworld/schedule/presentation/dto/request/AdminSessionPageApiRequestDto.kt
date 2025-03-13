package co.yappuworld.schedule.presentation.dto.request

import co.yappuworld.schedule.application.dto.request.AdminSessionPageAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema

data class AdminSessionPageApiRequestDto(
    @field:Schema(description = "페이지 번호", required = true)
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 수", required = true)
    val size: Int,
    @field:Schema(description = "기수", nullable = true)
    val generation: Int? = null
) {

    fun toAppRequest() =
        AdminSessionPageAppRequestDto(
            page = page,
            limit = size,
            generation = generation
        )
}
