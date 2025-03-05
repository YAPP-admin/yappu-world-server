package co.yappuworld.operation.presentation.dto.request

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.ClientPlatform
import co.yappuworld.operation.domain.ConfigError
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ClientVersionApiRequestDto(
    @Schema(description = "플랫폼 정보")
    @field:NotNull
    val platform: ClientPlatform,
    @Schema(description = "버전 정보 (x.y.z)")
    @field:NotBlank
    val version: String
) {
    init {
        val versions = version.split(".")
        if (versions.size != 3) {
            throw BusinessException(ConfigError.WRONG_VERSION_FORMAT)
        }
    }
}
