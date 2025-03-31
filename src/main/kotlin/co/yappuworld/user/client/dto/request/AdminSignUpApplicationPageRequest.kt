package co.yappuworld.user.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest

data class AdminSignUpApplicationPageRequest(
    @field:Schema(description = "페이지 번호", required = true)
    @field:Min(value = 1L)
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 개수", required = true)
    @field:Min(value = 1L)
    val size: Int
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size)
}
