package co.yappuworld.user.client.dto.response

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

data class AdminSignUpApplicationOverviewResponse(
    @Schema(description = "신청서 식별자")
    val id: UUID,
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "이메일")
    val email: String,
    @Schema(description = "지원 일시")
    val applicationDate: LocalDateTime,
    @Schema(description = "가장 최근 활동 내역")
    val activityUnit: AdminSignUpApplicationActivityUnitResponse,
    @Schema(description = "신청서 처리 상태", allowableValues = ["승인", "대기", "거절"])
    val status: String,
    @Schema(description = "처리 일시", nullable = true)
    val processDate: LocalDateTime?
) {

    constructor(application: SignUpApplicationEntity) : this(
        id = application.id,
        name = application.details.name,
        email = application.details.email,
        applicationDate = application.createdAt,
        activityUnit = AdminSignUpApplicationActivityUnitResponse(
            application.details.activityUnits.maxByOrNull { it.generation }
                ?: throw BusinessException(UserError.ACTIVE_UNIT_IS_ESSENTIAL)
        ),
        status = application.status.label,
        processDate = application.updatedAt.takeIf { application.status != SignUpApplicationStatus.PENDING }
    )
}
