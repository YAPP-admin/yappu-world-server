package co.yappuworld.operation.client.dto.response

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.ClientPlatform
import co.yappuworld.operation.domain.ConfigError
import co.yappuworld.operation.domain.Version
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger { }

data class AdminMinSupportVersionResponse(
    val platforms: List<AdminForceUpdateInfoByPlatformResponse>
) {

    companion object {

        /**
         * minSupportVersionInIos
         * minSupportVersionInAndroid
         */
        fun from(versionByPlatform: Map<String, Version>): AdminMinSupportVersionResponse =
            AdminMinSupportVersionResponse(
                listOf(
                    "minSupportVersionInIos" to ClientPlatform.IOS,
                    "minSupportVersionInAndroid" to ClientPlatform.ANDROID
                ).map { (key, platform) ->
                    if (!versionByPlatform.containsKey(key) || versionByPlatform[key] == null) {
                        logger.error { "플랫폼(${platform.label} 버전 최소 정보가 존재하지 않습니다." }
                        throw BusinessException(ConfigError.CONFIG_KEY_ERROR)
                    }

                    val version = requireNotNull(versionByPlatform[key])
                    AdminForceUpdateInfoByPlatformResponse(
                        platform = platform.label,
                        version = version.value
                    )
                }
            )
    }
}

data class AdminForceUpdateInfoByPlatformResponse(
    val platform: String,
    val version: String
)
