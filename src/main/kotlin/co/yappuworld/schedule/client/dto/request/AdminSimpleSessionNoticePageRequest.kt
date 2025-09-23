package co.yappuworld.schedule.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.UUID

data class AdminSimpleSessionNoticePageRequest(
    @field:Schema(description = "페이지 번호", required = true)
    @field:Min(value = 1L, message = "페이지 번호는 1 이상이어야 합니다.")
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 개수", required = true)
    @field:Min(value = 1L, message = "페이지 당 데이터 개수는 1 이상이어야 합니다.")
    val size: Int,
    @field:Schema(description = "세션 ID", required = true)
    val sessionId: UUID? = null,
    @field:Schema(description = "검색어", required = false, nullable = true)
    val search: String? = null
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"))
}
