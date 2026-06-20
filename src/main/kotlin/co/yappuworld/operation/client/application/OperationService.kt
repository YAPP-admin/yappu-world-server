package co.yappuworld.operation.client.application

import co.yappuworld.operation.client.dto.request.ForceUpdateInquiryRequest
import co.yappuworld.operation.client.dto.response.ActiveGenerationResponse
import co.yappuworld.operation.client.dto.response.ForceUpdateResponse
import co.yappuworld.operation.client.dto.response.GenerationResponse
import co.yappuworld.operation.client.dto.response.GenerationsResponse
import co.yappuworld.operation.client.dto.response.OperationLinkResponse
import co.yappuworld.operation.domain.ClientPlatform.ANDROID
import co.yappuworld.operation.domain.ClientPlatform.IOS
import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.operation.domain.Version
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OperationService(
    private val configFindService: ConfigFindService,
    private val generationFindService: GenerationFindService
) {

    @Transactional(readOnly = true)
    fun getForceUpdateInfo(request: ForceUpdateInquiryRequest): ForceUpdateResponse {
        val minSupportVersion = when (request.platform) {
            ANDROID -> findConfigBy("minSupportVersionInAndroid")
            IOS -> findConfigBy("minSupportVersionInIos")
        }.value

        return ForceUpdateResponse(
            minSupportVersion != null && request.version.isBeforeThan(Version(minSupportVersion))
        )
    }

    @Transactional(readOnly = true)
    fun getActiveGeneration(): ActiveGenerationResponse =
        findConfigBy("activeGeneration")
            .value
            .takeUnless { it.isNullOrBlank() }
            ?.let {
                ActiveGenerationResponse(true, it.toInt())
            } ?: ActiveGenerationResponse(false, null)

    @Transactional(readOnly = true)
    fun getGenerations(): GenerationsResponse =
        GenerationsResponse(
            generationFindService.findAllGenerations().map { GenerationResponse.from(it) }
        )

    @Transactional(readOnly = true)
    fun getOperationLink(configKey: String): OperationLinkResponse =
        OperationLinkResponse(findConfigBy(configKey).value)

    private fun findConfigBy(key: String): ConfigEntity =
        configFindService.findConfig(key)
            ?: throw NoSuchElementException("해당하는 값을 찾을 수 없습니다.")
}
