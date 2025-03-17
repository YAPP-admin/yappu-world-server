package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.application.AdminOperationService
import co.yappuworld.operation.application.dto.response.AdminOperationLinksResponse
import co.yappuworld.operation.presentation.dto.request.AdminMinSupportVersionUpdateRequest
import co.yappuworld.operation.presentation.dto.response.AdminMinSupportVersionApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminOperationController(
    private val adminOperationService: AdminOperationService
) : AdminOperationApi {

    override fun getMinSupportVersion(): ResponseEntity<SuccessResponse<AdminMinSupportVersionApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                AdminMinSupportVersionApiResponseDto(adminOperationService.getForceUpdateInfos())
            )
        )

    override fun updateMinSupportVersion(request: AdminMinSupportVersionUpdateRequest): ResponseEntity<Unit> {
        adminOperationService.updateMinimumSupportVersion(request)
        return ResponseEntity.noContent().build()
    }

    override fun getOperationLinks(): ResponseEntity<SuccessResponse<AdminOperationLinksResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                adminOperationService.getOperationLinks()
            )
        )
}
