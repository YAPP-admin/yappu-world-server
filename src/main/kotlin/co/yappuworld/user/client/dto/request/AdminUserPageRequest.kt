package co.yappuworld.user.client.dto.request

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest

data class AdminUserPageRequest(
    @field:Schema(description = "이름(검색어)")
    val name: String? = null,
    @field:Schema(description = "기수")
    val generation: Int? = null,
    @field:Schema(description = "직군")
    val position: Position? = null,
    @field:Schema(description = "권한")
    val role: UserRole? = null,
    @field:Schema(description = "페이지 수")
    @field:Min(value = 1L)
    val page: Int,
    @field:Schema(description = "페이지 당 데이터 개수")
    @field:Min(value = 1L)
    val size: Int
) {
    fun toPageRequest() = PageRequest.of(page - 1, size)
}
