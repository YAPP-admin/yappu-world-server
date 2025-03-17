package co.yappuworld.operation.presentation.dto.response

import co.yappuworld.operation.application.dto.response.AdminForceUpdateInfoAppResponseDto

data class AdminMinSupportVersionApiResponseDto(
    val platforms: List<AdminForceUpdateInfoByPlatformApiResponseDto>
) {

    constructor(response: AdminForceUpdateInfoAppResponseDto) : this(
        response.platforms.map {
            AdminForceUpdateInfoByPlatformApiResponseDto(it.platform.label, it.version.value)
        }
    )
}

data class AdminForceUpdateInfoByPlatformApiResponseDto(
    val platform: String,
    val version: String
)
