package co.yappuworld.schedule.presentation.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class AdminSessionPageApiRequestDto(
    @field:Schema(description = "페이지 번호")
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 수")
    val limit: Int,
    @field:Schema(description = "기수", nullable = true)
    val generation: Int?
)
