package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.model.User
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.SignUpApplicationStatus.APPROVED
import co.yappuworld.user.domain.vo.SignUpApplicationStatus.REJECTED

data class AdminSignUpApplicationAppResponseDto(
    val details: AdminSignUpApplicationDetailsAppResponseDto,
    val status: SignUpApplicationStatus,
    val rejectReason: String?,
    val assignedRole: UserRole?
) {

    constructor(application: SignUpApplication, user: User? = null) : this(
        details = AdminSignUpApplicationDetailsAppResponseDto(application),
        status = application.status,
        rejectReason = application.takeIf { it.status == REJECTED }?.rejectReason,
        assignedRole = user.takeIf { application.status == APPROVED }?.role
    )
}
