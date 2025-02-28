package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.UserProfileAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class UserProfileApiResponseDto(
    @Schema(description = "유저 식별자")
    val id: UUID,
    @Schema(description = "실명")
    val name: String,
    @Schema(
        description = "유저 유형",
        allowableValues = ["관리자", "운영진", "정회원", "수료회원", "활동회원"]
    )
    val role: String,
    @Schema(description = "기수, 직군 목록")
    val activityUnits: List<ActivityUnitApiResponseDto>
) {

    companion object {
        fun of(response: UserProfileAppResponseDto): UserProfileApiResponseDto {
            return UserProfileApiResponseDto(
                response.id,
                response.name,
                response.role.label,
                response.activityUnits
                    .map { ActivityUnitApiResponseDto(it) }
                    .sortedByDescending { it.generation }
            )
        }
    }
}
