package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.operation.domain.ClientPlatform
import co.yappuworld.operation.domain.ClientPlatform.ANDROID
import co.yappuworld.operation.domain.ClientPlatform.IOS
import co.yappuworld.operation.domain.Version
import co.yappuworld.operation.presentation.dto.response.ActiveGenerationApiResponseDto
import co.yappuworld.operation.presentation.dto.response.ForceUpdateApiResponseDto
import co.yappuworld.operation.presentation.dto.response.OperationLinkApiResponseDto
import co.yappuworld.operation.presentation.dto.response.PositionApiResponseDto
import co.yappuworld.operation.presentation.dto.response.PositionsApiResponseDto
import co.yappuworld.user.domain.vo.Position
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class OperationController(
    private val configInquiryComponent: ConfigInquiryComponent
) : OperationApi {

    override fun getPositions(): ResponseEntity<SuccessResponse<PositionsApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                PositionsApiResponseDto(
                    Position.entries.map { PositionApiResponseDto(it) }
                )
            )
        )

    override fun getForceUpdateInfo(
        version: Version,
        platform: ClientPlatform
    ): ResponseEntity<SuccessResponse<ForceUpdateApiResponseDto>> {
        val minSupportVersion = when (platform) {
            ANDROID -> configInquiryComponent.findConfigBy("minSupportVersionInAndroid")
            IOS -> configInquiryComponent.findConfigBy("minSupportVersionInIos")
        }.value

        return ResponseEntity.ok(
            SuccessResponse(
                ForceUpdateApiResponseDto(
                    minSupportVersion != null && version.isBeforeThan(Version(minSupportVersion))
                )
            )
        )
    }

    override fun getActiveGeneration(): ResponseEntity<SuccessResponse<ActiveGenerationApiResponseDto>> {
        val response = configInquiryComponent
            .findConfigBy("activeGeneration")
            .value
            .takeUnless { it.isNullOrBlank() }
            ?.let {
                ActiveGenerationApiResponseDto(true, it.toInt())
            } ?: ActiveGenerationApiResponseDto(false, null)

        return ResponseEntity.ok(
            SuccessResponse(response)
        )
    }

    override fun getUsageInquiryLink(): ResponseEntity<SuccessResponse<OperationLinkApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                OperationLinkApiResponseDto(
                    configInquiryComponent.findConfigBy("usageInquiryLink").value
                )
            )
        )

    override fun getTermsOfServiceLink(): ResponseEntity<SuccessResponse<OperationLinkApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                OperationLinkApiResponseDto(
                    configInquiryComponent.findConfigBy("termsOfServiceLink").value
                )
            )
        )

    override fun getPrivacyPolicyLink(): ResponseEntity<SuccessResponse<OperationLinkApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                OperationLinkApiResponseDto(
                    configInquiryComponent.findConfigBy("privacyPolicyLink").value
                )
            )
        )
}
