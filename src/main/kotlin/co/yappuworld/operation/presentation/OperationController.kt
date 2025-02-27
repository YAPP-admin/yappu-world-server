package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.application.ConfigInquiryComponent
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

    override fun getPositions(): ResponseEntity<SuccessResponse<PositionsApiResponseDto>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                PositionsApiResponseDto(
                    Position.entries.map { PositionApiResponseDto.of(it) }
                )
            )
        )
    }

    override fun getForceUpdateInfo(): ResponseEntity<SuccessResponse<ForceUpdateApiResponseDto>> {
        val configs = configInquiryComponent.findConfigsBy(listOf("needForceUpdate", "forceUpdateReason"))
        return ResponseEntity.ok(
            SuccessResponse.of(
                ForceUpdateApiResponseDto.of(configs)
            )
        )
    }

    override fun getActiveGeneration(): ResponseEntity<SuccessResponse<ActiveGenerationApiResponseDto>> {
        val activeGenerationResponse = configInquiryComponent.findConfigBy("activeGeneration").let {
            when (it.value?.isNotBlank()) {
                true -> ActiveGenerationApiResponseDto(true, it.value.toInt())
                null, false -> ActiveGenerationApiResponseDto(false, null)
            }
        }

        return ResponseEntity.ok(
            SuccessResponse.of(activeGenerationResponse)
        )
    }

    override fun getUsageInquiryLink(): ResponseEntity<SuccessResponse<OperationLinkApiResponseDto>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                OperationLinkApiResponseDto(
                    configInquiryComponent.findConfigBy("usageInquiryLink").value
                )
            )
        )
    }

    override fun getTermsOfServiceLink(): ResponseEntity<SuccessResponse<OperationLinkApiResponseDto>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                OperationLinkApiResponseDto(
                    configInquiryComponent.findConfigBy("termsOfServiceLink").value
                )
            )
        )
    }

    override fun getPrivacyPolicyLink(): ResponseEntity<SuccessResponse<OperationLinkApiResponseDto>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                OperationLinkApiResponseDto(
                    configInquiryComponent.findConfigBy("privacyPolicyLink").value
                )
            )
        )
    }
}
