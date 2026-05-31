package co.yappuworld.user.client.dto.response

import co.yappuworld.user.infrastructure.entity.UserEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.user.infrastructure.model.UserPersonProfileHistoryProjection
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class UserPersonProfileResponse(
    @field:Schema(description = "유저 식별자")
    val userId: UUID,
    @field:Schema(description = "이름")
    val name: String,
    @field:Schema(description = "권한")
    val role: String,
    @field:Schema(description = "가장 최근 활동 정보", nullable = true)
    val latestActivity: UserPersonLatestActivityResponse?,
    @field:Schema(description = "역대 활동 내역")
    val histories: List<UserPersonHistoryResponse>
) {
    companion object {
        fun of(
            user: UserEntity,
            histories: List<UserPersonHistoryResponse>
        ): UserPersonProfileResponse =
            UserPersonProfileResponse(
                userId = user.id,
                name = user.name,
                role = user.role.label,
                latestActivity = histories.firstOrNull()?.let(UserPersonLatestActivityResponse::from),
                histories = histories
            )
    }
}

data class UserPersonLatestActivityResponse(
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "직군")
    val position: String
) {
    companion object {
        fun from(history: UserPersonHistoryResponse): UserPersonLatestActivityResponse =
            UserPersonLatestActivityResponse(
                generation = history.generation,
                position = history.position
            )
    }
}

data class UserPersonHistoryResponse(
    @field:Schema(description = "기수")
    val generation: Int,
    @field:Schema(description = "직군")
    val position: String,
    @field:Schema(description = "활동 시작일", nullable = true)
    val activityStartDate: LocalDate?,
    @field:Schema(description = "활동 종료일", nullable = true)
    val activityEndDate: LocalDate?,
    @field:Schema(description = "서비스 정보", nullable = true)
    val service: UserPersonHistoryServiceResponse?
) {
    companion object {
        fun from(
            projection: UserPersonProfileHistoryProjection,
            service: TeamServiceEntity?
        ): UserPersonHistoryResponse =
            UserPersonHistoryResponse(
                generation = projection.generation,
                position = projection.position.label,
                activityStartDate = projection.activityStartDate,
                activityEndDate = projection.activityEndDate,
                service = if (projection.teamName != null && projection.serviceId != null) {
                    UserPersonHistoryServiceResponse.from(projection, service)
                } else {
                    null
                }
            )
    }
}

data class UserPersonHistoryServiceResponse(
    @field:Schema(description = "서비스 ID")
    val serviceId: UUID,
    @field:Schema(description = "팀명")
    val teamName: String,
    @field:Schema(description = "서비스명")
    val serviceName: String?,
    @field:Schema(description = "한 줄 소개")
    val summary: String?,
    @field:Schema(description = "앱 플랫폼 여부")
    val hasApp: Boolean,
    @field:Schema(description = "웹 플랫폼 여부")
    val hasWeb: Boolean,
    @field:Schema(description = "구글 플레이 링크")
    val googlePlayLink: String?,
    @field:Schema(description = "앱스토어 링크")
    val appStoreLink: String?,
    @field:Schema(description = "웹 링크")
    val webLink: String?,
    @field:Schema(description = "썸네일 이미지 URL")
    val thumbnailImageUrl: String?
) {
    companion object {
        fun from(
            projection: UserPersonProfileHistoryProjection,
            service: TeamServiceEntity?
        ): UserPersonHistoryServiceResponse =
            UserPersonHistoryServiceResponse(
                serviceId = requireNotNull(projection.serviceId),
                teamName = requireNotNull(projection.teamName),
                serviceName = projection.serviceName,
                summary = projection.summary,
                hasApp = projection.hasApp ?: false,
                hasWeb = projection.hasWeb ?: false,
                googlePlayLink = service?.serviceLinks?.googlePlay,
                appStoreLink = service?.serviceLinks?.appStore,
                webLink = service?.serviceLinks?.web,
                thumbnailImageUrl = null
            )
    }
}
