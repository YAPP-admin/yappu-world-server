package co.yappuworld.user.client.dto.response

import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class AdminUserDetailResponse(
    @Schema(description = "유저 식별자")
    val id: UUID,
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "이메일")
    val email: String,
    @Schema(description = "전화번호", nullable = true)
    val phoneNumber: String?,
    @Schema(description = "성별", nullable = true, allowableValues = ["남", "여"])
    val gender: String?,
    @Schema(description = "역할")
    val role: String,
    @Schema(description = "계정 살아있는 여부(F = 탈퇴)")
    val isActive: Boolean,
    @Schema(description = "가입일")
    val registrationDate: LocalDate,
    @Schema(description = "활동 내역")
    val activityUnits: List<AdminUserDetailActivityUnitResponse>
) {

    constructor(
        user: UserEntity,
        activityUnits: List<ActivityUnitEntity>,
        activeGeneration: Int?,
        services: Map<UUID, TeamServiceEntity>
    ) : this(
        id = user.id,
        name = user.name,
        email = user.email,
        phoneNumber = null,
        gender = null,
        role = user.role.label,
        isActive = user.isActive,
        registrationDate = user.createdAt.toLocalDate(),
        activityUnits = activityUnits
            .map {
                AdminUserDetailActivityUnitResponse(
                    activityUnit = it,
                    activeGeneration = activeGeneration,
                    service = services[it.teamMember?.team?.id]
                )
            }.sortedByDescending { it.generation }
    )
}

data class AdminUserDetailActivityUnitResponse(
    @Schema(description = "ID")
    val id: UUID,
    @Schema(description = "기수", minContains = 1)
    val generation: Int,
    @Schema(description = "직군")
    val position: String,
    @Schema(description = "활동 중인지 여부")
    val isActive: Boolean,
    @Schema(description = "팀 정보")
    val team: AdminUserDetailTeamResponse? = null
) {

    constructor(
        activityUnit: ActivityUnitEntity,
        activeGeneration: Int?,
        service: TeamServiceEntity?
    ) : this(
        id = activityUnit.id,
        generation = activityUnit.generation,
        position = activityUnit.position.label,
        isActive = activityUnit.generation == activeGeneration,
        team = AdminUserDetailTeamResponse.from(activityUnit, service)
    )
}

data class AdminUserDetailTeamResponse(
    @Schema(description = "팀 ID")
    val id: UUID,
    @Schema(description = "팀 이름")
    val name: String,
    @Schema(description = "서비스 ID")
    val serviceId: UUID?,
    @Schema(description = "서비스 이름")
    val serviceName: String?
) {
    companion object {
        fun from(
            activityUnit: ActivityUnitEntity,
            service: TeamServiceEntity?
        ): AdminUserDetailTeamResponse? {
            val team = activityUnit.teamMember?.team ?: return null

            return AdminUserDetailTeamResponse(
                id = team.id,
                name = team.name,
                serviceId = service?.id,
                serviceName = service?.name
            )
        }
    }
}
