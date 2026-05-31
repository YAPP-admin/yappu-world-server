package co.yappuworld.team.client.dto.request

import co.yappuworld.team.domain.vo.Platform
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import java.util.UUID

@Schema(description = "역대 서비스 목록 조회 요청")
data class HistoricalServicesPageRequest(
    @field:Schema(description = "지난 요청 마지막 데이터의 ID, 첫 요청인 경우 null", required = false)
    val lastCursorId: UUID? = null,
    @field:Schema(description = "요청 데이터 개수", required = true, example = "20", defaultValue = "20")
    @field:Min(value = 1L, message = "요청 데이터 개수는 최소 1개 이상이어야 합니다.")
    val limit: Int = 20,
    @field:Schema(description = "기수 필터", example = "25")
    @field:Min(value = 1L, message = "기수는 1 이상이어야 합니다.")
    val generation: Int? = null,
    @field:Schema(description = "플랫폼 필터 (APP/WEB)", example = "APP")
    val platform: Platform? = null
)
