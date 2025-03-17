package co.yappuworld.operation.application

import co.yappuworld.operation.application.dto.response.AdminForceUpdateInfoAppResponseDto
import co.yappuworld.operation.application.dto.response.AdminOperationLinksResponse
import co.yappuworld.operation.domain.ClientPlatform.ANDROID
import co.yappuworld.operation.domain.ClientPlatform.IOS
import co.yappuworld.operation.domain.ConfigCategory
import co.yappuworld.operation.infrastructure.ConfigRepository
import co.yappuworld.operation.presentation.dto.request.AdminMinSupportVersionUpdateRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminOperationService(
    private val configRepository: ConfigRepository
) {

    @Transactional(readOnly = true)
    fun getForceUpdateInfos(): AdminForceUpdateInfoAppResponseDto =
        AdminForceUpdateInfoAppResponseDto.from(
            configRepository
                .findByCategory(ConfigCategory.FORCE_UPDATE)
                .associateBy { it.id }
        )

    @Transactional
    fun updateMinimumSupportVersion(request: AdminMinSupportVersionUpdateRequest) {
        when (request.platform) {
            ANDROID -> configRepository.findByIdOrNull("minSupportVersionInAndroid")
            IOS -> configRepository.findByIdOrNull("minSupportVersionInIos")
        }?.apply { updateValue(request.version.value) }
            ?.also { configRepository.save(it) }
    }

    @Transactional(readOnly = true)
    fun getOperationLinks(): AdminOperationLinksResponse =
        configRepository
            .findByCategory(ConfigCategory.LINK)
            .let { AdminOperationLinksResponse.from(it) }
}
