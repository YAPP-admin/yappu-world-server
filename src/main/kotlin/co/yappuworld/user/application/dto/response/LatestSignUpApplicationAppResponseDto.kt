package co.yappuworld.user.application.dto.response

import co.yappuworld.user.domain.model.SignUpApplication
import co.yappuworld.user.domain.vo.UserSignUpApplicationStatus

data class LatestSignUpApplicationAppResponseDto(
    val status: UserSignUpApplicationStatus,
    val rejectReason: String?
) {
    companion object {
        fun of(application: SignUpApplication): LatestSignUpApplicationAppResponseDto {
            return LatestSignUpApplicationAppResponseDto(
                application.status,
                application.rejectReason
            )
        }
    }
}
