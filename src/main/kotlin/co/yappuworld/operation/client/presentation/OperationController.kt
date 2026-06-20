package co.yappuworld.operation.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.client.application.OperationService
import co.yappuworld.operation.client.dto.request.ForceUpdateInquiryRequest
import co.yappuworld.operation.client.dto.response.ActiveGenerationResponse
import co.yappuworld.operation.client.dto.response.ForceUpdateResponse
import co.yappuworld.operation.client.dto.response.GenerationsResponse
import co.yappuworld.operation.client.dto.response.OperationLinkResponse
import co.yappuworld.operation.client.dto.response.PositionResponse
import co.yappuworld.operation.client.dto.response.PositionsResponse
import co.yappuworld.user.domain.vo.Position
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class OperationController(
    private val operationService: OperationService
) : OperationApi {

    override fun getPositions(): ResponseEntity<SuccessResponse<PositionsResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                PositionsResponse(
                    Position.entries.map { PositionResponse(it) }
                )
            )
        )

    override fun getGenerations(): ResponseEntity<SuccessResponse<GenerationsResponse>> =
        ResponseEntity.ok(
            SuccessResponse(operationService.getGenerations())
        )

    override fun getForceUpdateInfo(
        request: ForceUpdateInquiryRequest
    ): ResponseEntity<SuccessResponse<ForceUpdateResponse>> =
        ResponseEntity.ok(
            SuccessResponse(operationService.getForceUpdateInfo(request))
        )

    override fun getActiveGeneration(): ResponseEntity<SuccessResponse<ActiveGenerationResponse>> =
        ResponseEntity.ok(
            SuccessResponse(operationService.getActiveGeneration())
        )

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
            SuccessResponse(operationService.getOperationLink(configKey))
        )
}
