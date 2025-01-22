package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.LatestSignUpApplicationAppResponseDto
import co.yappuworld.user.domain.UserSignUpApplicationStatus
import io.swagger.v3.oas.annotations.media.Schema

data class LatestSignUpApplicationApiResponseDto(
    @Schema(
        description = "가장 최근 회원가입 신청의 처리 상태",
        examples = ["REJECTED", "APPROVED", "PENDING"]
    )
    val status: UserSignUpApplicationStatus,
    @Schema(description = "거절 사유", nullable = true)
    val rejectReason: String?
) {
    companion object {
        fun of(response: LatestSignUpApplicationAppResponseDto): LatestSignUpApplicationApiResponseDto {
            return LatestSignUpApplicationApiResponseDto(
                response.status,
                response.rejectReason
            )
        }
    }
}
