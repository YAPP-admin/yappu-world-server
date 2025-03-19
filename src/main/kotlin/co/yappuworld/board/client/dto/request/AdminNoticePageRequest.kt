package co.yappuworld.board.client.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest

data class AdminNoticePageRequest(
    @field:Schema(description = "페이지 번호", required = true)
    @field:Min(value = 1L, message = "페이지 번호는 1 이상이어야 합니다.")
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 개수", required = true)
    @field:Min(value = 1L, message = "페이지 당 데이터 개수는 1 이상이어야 합니다.")
    val size: Int,
    @field:Schema(description = "공지 타입", allowableValues = ["ALL", "SESSION", "OPERATION"], required = true)
    val noticeType: String
) {

    fun toPageRequest(): PageRequest = PageRequest.of(page - 1, size)
}
