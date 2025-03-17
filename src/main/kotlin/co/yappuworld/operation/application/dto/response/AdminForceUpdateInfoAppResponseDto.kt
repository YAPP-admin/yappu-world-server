package co.yappuworld.operation.application.dto.response

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.ClientPlatform
import co.yappuworld.operation.domain.Config
import co.yappuworld.operation.domain.ConfigError
import co.yappuworld.operation.domain.Version

data class AdminForceUpdateInfoAppResponseDto(
    val platforms: List<AdminForceUpdateInfoByPlatformAppResponseDto>
) {

    companion object {

        /**
         * minSupportVersionInIos
         * minSupportVersionInAndroid
         */
        fun from(configs: Map<String, Config>): AdminForceUpdateInfoAppResponseDto =
            AdminForceUpdateInfoAppResponseDto(
                listOf(
                    "minSupportVersionInIos" to ClientPlatform.IOS,
                    "minSupportVersionInAndroid" to ClientPlatform.ANDROID
                ).map { (key, platform) ->
                    if (!configs.containsKey(key)) {
                        throw BusinessException(ConfigError.CONFIG_KEY_ERROR)
                    }

                    val config = configs[key]!!

                    if (config.value == null) {
                        throw BusinessException(ConfigError.MIN_VERSION_CANNOT_NULL)
                    }

                    AdminForceUpdateInfoByPlatformAppResponseDto(
                        platform,
                        Version(config.value!!)
                    )
                }
            )
    }
}

data class AdminForceUpdateInfoByPlatformAppResponseDto(
    val platform: ClientPlatform,
    val version: Version
)
