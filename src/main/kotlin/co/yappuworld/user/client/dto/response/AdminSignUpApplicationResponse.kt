package co.yappuworld.user.client.dto.response

import co.yappuworld.user.domain.entity.SignUpApplicationEntity
import co.yappuworld.user.domain.entity.UserEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus.APPROVED
import co.yappuworld.user.domain.vo.SignUpApplicationStatus.REJECTED
import io.swagger.v3.oas.annotations.media.Schema

data class AdminSignUpApplicationResponse(
    @Schema(description = "신청서 세부 내역")
    val details: AdminSignUpApplicationDetailsResponse,
    @Schema(description = "신청서 처리 상태", allowableValues = ["승인", "대기", "거절"])
    val status: String,
    @Schema(description = "거절 사유")
    val rejectReason: String?,
    @Schema(description = "가입 승인되었을 때 유저에게 할당된 역할")
    val assignedRole: UserRoleResponse?
) {

    constructor(application: SignUpApplicationEntity, user: UserEntity? = null) : this(
        details = AdminSignUpApplicationDetailsResponse(application),
        status = application.status.label,
        rejectReason = application.takeIf { it.status == REJECTED }?.rejectReason,
        assignedRole = user.takeIf { application.status == APPROVED }?.let { UserRoleResponse(it.role) }
    )
}
