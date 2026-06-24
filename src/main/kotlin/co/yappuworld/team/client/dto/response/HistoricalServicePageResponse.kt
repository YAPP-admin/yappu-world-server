package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.dto.HistoricalServiceSummaryProjection
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class HistoricalServicePageResponse(
    @field:Schema(description = "서비스 ID")
    val serviceId: UUID,
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "서비스명")
    val serviceName: String?,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "한 줄 소개")
    val summary: String?,
    @field:Schema(description = "썸네일 이미지 URL")
    val thumbnailImageUrl: String?
) {
    companion object {
        fun from(
            projection: HistoricalServiceSummaryProjection,
            thumbnailImageUrl: String?
        ): HistoricalServicePageResponse =
            HistoricalServicePageResponse(
                serviceId = projection.serviceId,
                generation = projection.generation,
                serviceName = projection.serviceName,
                hasApp = projection.hasApp,
                hasWeb = projection.hasWeb,
                summary = projection.summary,
                thumbnailImageUrl = thumbnailImageUrl
            )
    }
}
