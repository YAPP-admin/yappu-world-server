package co.yappuworld.operation.client.dto.request

import co.yappuworld.operation.domain.ClientPlatform
import co.yappuworld.operation.domain.Version
import io.swagger.v3.oas.annotations.media.Schema

data class ForceUpdateInquiryRequest(
    @field:Schema(name = "version", description = "현재 클라이언트 버전", example = "1.2.3", required = true)
    val version: Version,
    @field:Schema(description = "클라이언트 플랫폼", required = true)
    val platform: ClientPlatform
)
