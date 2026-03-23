package co.yappuworld.schedule.client.dto.request

import co.yappuworld.schedule.domain.vo.SessionType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest

data class AdminSessionPageRequest(
    @field:Schema(description = "제목(검색어)", nullable = true, example = "오프라인")
    val title: String? = null,
    @field:Schema(description = "세션 종류", nullable = true, example = "OFFLINE")
    val type: SessionType? = null,
    @field:Schema(description = "페이지 번호", required = true, example = "1")
    @field:Min(value = 1L, message = "페이지 번호는 1 이상이어야 합니다.")
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 수", required = true, example = "20")
    @field:Min(value = 1L, message = "페이지 당 데이터 수는 1 이상이어야 합니다.")
    val size: Int,
    @field:Schema(description = "기수", nullable = true, example = "25")
    @field:Min(value = 1L, message = "기수는 1 이상이어야 합니다.")
    val generation: Int? = null
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size)
}
