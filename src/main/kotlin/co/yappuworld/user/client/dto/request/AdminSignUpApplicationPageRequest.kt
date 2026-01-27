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
    val size: Int,
    @field:Schema(description = "이름 검색어", example = "홍길동")
    val name: String? = null,
    @field:Schema(description = "상태 필터", example = "PENDING")
    val status: String? = null,
    @field:Schema(description = "직군 필터", example = "PM")
    val position: String? = null,
    @field:Schema(description = "기수 필터", example = "6")
    val generation: Int? = null
) {
    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size)
}
