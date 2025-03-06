package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.vo.SignUpApplicationStatus

data class LatestSignUpApplicationAppResponseDto(
    val status: SignUpApplicationStatus,
    val rejectReason: String?
) {
    companion object {
        fun of(application: SignUpApplication): LatestSignUpApplicationAppResponseDto =
            LatestSignUpApplicationAppResponseDto(
                application.status,
                application.rejectReason
            )
    }
}
