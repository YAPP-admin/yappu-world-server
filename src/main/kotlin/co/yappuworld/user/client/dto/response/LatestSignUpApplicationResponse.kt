package co.yappuworld.user.client.dto.response

import co.yappuworld.user.domain.entity.SignUpApplicationEntity
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import io.swagger.v3.oas.annotations.media.Schema

data class LatestSignUpApplicationResponse(
    @Schema(description = "가장 최근 회원가입 신청의 처리 상태")
    val status: SignUpApplicationStatus,
    @Schema(description = "거절 사유", nullable = true)
    val rejectReason: String?
) {

    constructor(application: SignUpApplicationEntity) : this(
        application.status,
        application.rejectReason
    )
}
