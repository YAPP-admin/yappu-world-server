package co.yappuworld.operation.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.client.dto.request.AdminOperationLinkUpdateRequest
import co.yappuworld.operation.client.dto.response.AdminOperationLinksResponse
import co.yappuworld.operation.client.dto.request.AdminMinSupportVersionUpdateRequest
import co.yappuworld.operation.client.dto.response.AdminMinSupportVersionResponse
import co.yappuworld.operation.domain.ClientPlatform.ANDROID
import co.yappuworld.operation.domain.ClientPlatform.IOS
import co.yappuworld.operation.domain.ConfigCategory
import co.yappuworld.operation.domain.ConfigError
import co.yappuworld.operation.domain.Version
import co.yappuworld.operation.infrastructure.ConfigRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminOperationService(
    private val configRepository: ConfigRepository
) {

    @Transactional(readOnly = true)
    fun getForceUpdateInfos(): AdminMinSupportVersionResponse =
        AdminMinSupportVersionResponse.from(
            configRepository
                .findByCategory(ConfigCategory.FORCE_UPDATE)
                .associateBy(
                    { it.id },
                    {
                        it.value?.let { v -> Version(v) }
                            ?: throw BusinessException(ConfigError.MIN_VERSION_CANNOT_NULL)
                    }
                )
        )

    @Transactional
    fun updateMinimumSupportVersion(request: AdminMinSupportVersionUpdateRequest) {
        when (request.platform) {
            ANDROID -> configRepository.findByIdOrNull("minSupportVersionInAndroid")
            IOS -> configRepository.findByIdOrNull("minSupportVersionInIos")
        }?.apply { update(request.version.value) }
            ?.also { configRepository.save(it) }
    }

    @Transactional(readOnly = true)
    fun getOperationLinks(): AdminOperationLinksResponse =
        configRepository
            .findByCategory(ConfigCategory.LINK)
            .let { AdminOperationLinksResponse.from(it) }

    @Transactional
    fun updateOperationLink(request: AdminOperationLinkUpdateRequest) {
        configRepository
            .findByIdOrNull(request.id)
            ?.apply { update(request.name, request.link) }
            ?.also { configRepository.save(it) }
    }
}
