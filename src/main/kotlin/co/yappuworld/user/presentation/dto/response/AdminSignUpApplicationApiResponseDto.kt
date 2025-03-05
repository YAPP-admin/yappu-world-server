package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.AdminSignUpApplicationAppResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class AdminSignUpApplicationApiResponseDto(
    @Schema(description = "신청서 세부 내역")
    val details: AdminSignUpApplicationDetailsApiResponseDto,
    @Schema(description = "신청서 처리 상태", allowableValues = ["승인", "대기", "거절"])
    val status: String,
    @Schema(description = "거절 사유")
    val rejectReason: String?,
    @Schema(description = "가입 승인되었을 때 유저에게 할당된 역할")
    val assignedRole: UserRoleApiResponseDto?
) {

    constructor(response: AdminSignUpApplicationAppResponseDto) : this(
        details = AdminSignUpApplicationDetailsApiResponseDto(response.details),
        status = response.status.label,
        rejectReason = response.rejectReason,
        assignedRole = response.assignedRole?.let { UserRoleApiResponseDto(response.assignedRole) }
    )
}
