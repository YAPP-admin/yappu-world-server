package co.yappuworld.operation.client.dto.request

import co.yappuworld.operation.domain.ClientPlatform
import co.yappuworld.operation.domain.Version
import io.swagger.v3.oas.annotations.media.Schema

data class AdminMinSupportVersionUpdateRequest(
    @Schema(description = "플랫폼")
    val platform: ClientPlatform,
    @Schema(description = "최소 지원 버전", example = "1.2.3", required = true)
    val version: Version
)
