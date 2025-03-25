package co.yappuworld.user.client.dto.response

import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import io.swagger.v3.oas.annotations.media.Schema

data class SignUpApplicationStatusResponse(
    @Schema(description = "요청 시 사용")
    val name: String,
    @Schema(description = "화면에 노출 시 사용")
    val label: String
) {

    constructor(status: SignUpApplicationStatus) : this(
        name = status.name,
        label = status.label
    )
}
