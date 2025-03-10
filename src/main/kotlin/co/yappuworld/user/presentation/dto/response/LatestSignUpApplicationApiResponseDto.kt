package co.yappuworld.user.presentation.dto.response

import co.yappuworld.user.application.dto.response.LatestSignUpApplicationAppResponseDto
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import io.swagger.v3.oas.annotations.media.Schema

data class LatestSignUpApplicationApiResponseDto(
    @Schema(description = "가장 최근 회원가입 신청의 처리 상태")
    val status: SignUpApplicationStatus,
    @Schema(description = "거절 사유", nullable = true)
    val rejectReason: String?
) {
    companion object {
        fun of(response: LatestSignUpApplicationAppResponseDto): LatestSignUpApplicationApiResponseDto =
            LatestSignUpApplicationApiResponseDto(
                response.status,
                response.rejectReason
            )
    }
}
