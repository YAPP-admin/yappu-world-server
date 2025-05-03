package co.yappuworld.user.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest

data class AdminUserPageRequest(
    @Schema(description = "페이지 수")
    @field:Min(value = 1L)
    val page: Int,
    @Schema(description = "페이지 당 데이터 개수")
    @field:Min(value = 1L)
    val size: Int
) {
    fun toPageRequest() = PageRequest.of(page - 1, size)
}
