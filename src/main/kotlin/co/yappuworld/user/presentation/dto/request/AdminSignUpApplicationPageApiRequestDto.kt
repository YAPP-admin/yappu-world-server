package co.yappuworld.user.presentation.dto.request

import co.yappuworld.user.application.dto.request.AdminSignUpApplicationPageAppRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

data class AdminSignUpApplicationPageApiRequestDto(
    @field:Schema(description = "페이지 번호", required = true)
    @field:Min(value = 1L)
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 개수", required = true)
    @field:Min(value = 1L)
    val size: Int
) {

    fun toAppRequest(): AdminSignUpApplicationPageAppRequestDto {
        return AdminSignUpApplicationPageAppRequestDto(
            page = page,
            size = size
        )
    }
}
