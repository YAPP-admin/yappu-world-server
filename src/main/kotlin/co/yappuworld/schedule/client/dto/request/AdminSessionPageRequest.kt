package co.yappuworld.schedule.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.PageRequest

data class AdminSessionPageRequest(
    @field:Schema(description = "페이지 번호", required = true)
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 수", required = true)
    val size: Int,
    @field:Schema(description = "기수", nullable = true)
    val generation: Int? = null
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size)
}
