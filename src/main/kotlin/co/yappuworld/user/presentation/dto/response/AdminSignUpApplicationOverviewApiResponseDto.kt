package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.AdminSignUpApplicationOverviewAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

data class AdminSignUpApplicationOverviewApiResponseDto(
    @Schema(description = "신청서 식별자")
    val applicationId: UUID,
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "이메일")
    val email: String,
    @Schema(description = "지원일")
    val applicationDate: LocalDate,
    @Schema(description = "가장 최근 활동 내역")
    val activityUnit: AdminSignUpApplicationActivityUnitApiResponseDto,
    @Schema(description = "신청서 처리 상태", allowableValues = ["승인", "대기", "거절"])
    val status: String
) {

    constructor(response: AdminSignUpApplicationOverviewAppResponseDto) : this(
        applicationId = response.applicationId,
        name = response.name,
        email = response.email,
        applicationDate = response.applicationDate,
        activityUnit = AdminSignUpApplicationActivityUnitApiResponseDto(response.activityUnit),
        status = response.status.label
    )
}
