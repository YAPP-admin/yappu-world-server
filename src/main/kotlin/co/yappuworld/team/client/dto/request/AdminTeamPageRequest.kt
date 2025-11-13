package co.yappuworld.team.client.dto.request

import co.yappuworld.team.domain.vo.ServicePlatform
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@Schema(description = "팀 목록 조회 페이지 요청")
data class AdminTeamPageRequest(
    @field:Schema(description = "기수 필터", example = "35")
    val generation: Int? = null,
    @field:Schema(description = "플랫폼 필터 (APP/WEB)", example = "APP")
    val platform: ServicePlatform? = null,
    @field:Schema(description = "페이지 번호", required = true, example = "1")
    @field:Min(value = 1L, message = "페이지 번호는 1 이상이어야 합니다.")
    val page: Int = 1,
    @field:Schema(description = "페이지 당 데이터 개수", required = true, example = "20")
    @field:Min(value = 1L, message = "데이터는 1개 이상이어야 합니다.")
    val size: Int = 20
) {

    fun toPageRequest(): PageRequest =
        PageRequest
            .of(page - 1, size)
            .withSort(
                Sort
                    .by(Sort.Direction.DESC, "generation")
                    .and(Sort.by(Sort.Direction.DESC, "name"))
            )
}
