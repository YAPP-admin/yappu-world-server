package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.AdminUserPageAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class AdminUserPageApiRequestDto(
    @Schema(description = "페이지 수")
    @field:Min(value = 1L)
    val page: Int,
    @Schema(description = "페이지 당 데이터 개수")
    @field:Min(value = 1L)
    val size: Int
) {

    fun toAppRequest(): AdminUserPageAppRequestDto {
        return AdminUserPageAppRequestDto(
            offset = size * (page - 1),
            limit = size
        )
    }
}
