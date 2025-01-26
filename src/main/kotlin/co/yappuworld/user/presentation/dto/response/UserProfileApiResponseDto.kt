package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.UserProfileAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class UserProfileApiResponseDto(
    @Schema(description = "유저 식별자")
    val id: UUID,
    @Schema(description = "실명")
    val name: String,
    @Schema(description = "유저 유형")
    val role: String,
    @Schema(description = "기수, 직군 목록")
    val activityUnits: List<ActivityUnitApiResponseDto>
) {

    companion object {
        fun of(response: UserProfileAppResponseDto): UserProfileApiResponseDto {
            return UserProfileApiResponseDto(
                response.id,
                response.name,
                response.role,
                response.activityUnits.map { ActivityUnitApiResponseDto.of(it) }
            )
        }
    }
}
