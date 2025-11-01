package co.yappuworld.operation.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.client.application.ConfigInquiryComponent
import co.yappuworld.operation.client.dto.request.ForceUpdateInquiryRequest
import co.yappuworld.operation.client.dto.response.ActiveGenerationResponse
import co.yappuworld.operation.client.dto.response.ForceUpdateResponse
import co.yappuworld.operation.client.dto.response.OperationLinkResponse
import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.operation.client.dto.response.PositionsResponse
import co.yappuworld.operation.domain.ClientPlatform.ANDROID
import co.yappuworld.operation.domain.ClientPlatform.IOS
import co.yappuworld.operation.domain.Version
import co.yappuworld.user.domain.vo.Position
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class OperationController(
    private val configInquiryComponent: ConfigInquiryComponent
) : OperationApi {

    override fun getPositions(): ResponseEntity<SuccessResponse<PositionsResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                PositionsResponse(
                    Position.entries.map { PositionResponse(it) }
                )
            )
        )

    override fun getForceUpdateInfo(
        request: ForceUpdateInquiryRequest
    ): ResponseEntity<SuccessResponse<ForceUpdateResponse>> {
        val minSupportVersion = when (request.platform) {
            ANDROID -> configInquiryComponent.findConfigBy("minSupportVersionInAndroid")
            IOS -> configInquiryComponent.findConfigBy("minSupportVersionInIos")
        }.value

        return ResponseEntity.ok(
            SuccessResponse(
                ForceUpdateResponse(
                    minSupportVersion != null && request.version.isBeforeThan(Version(minSupportVersion))
                )
            )
        )
    }

    override fun getActiveGeneration(): ResponseEntity<SuccessResponse<ActiveGenerationResponse>> {
        val response = configInquiryComponent
            .findConfigBy("activeGeneration")
            .value
            .takeUnless { it.isNullOrBlank() }
            ?.let {
                ActiveGenerationResponse(true, it.toInt())
            } ?: ActiveGenerationResponse(false, null)

        return ResponseEntity.ok(
            SuccessResponse(response)
        )
    }

    override fun getUsageInquiryLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>> =
        getLinkByConfigKey("usageInquiryLink")

    override fun getTermsOfServiceLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>> =
        getLinkByConfigKey("termsOfServiceLink")

    override fun getPrivacyPolicyLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>> =
        getLinkByConfigKey("privacyPolicyLink")

    override fun getBasicRuleLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>> =
        getLinkByConfigKey("basicRuleLink")

    private fun getLinkByConfigKey(configKey: String): ResponseEntity<SuccessResponse<OperationLinkResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                OperationLinkResponse(
                    configInquiryComponent.findConfigBy(configKey).value
                )
            )
        )
}
