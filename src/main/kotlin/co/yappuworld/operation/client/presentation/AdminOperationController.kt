package co.yappuworld.operation.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.client.application.AdminOperationService
import co.yappuworld.operation.client.dto.request.AdminOperationLinkUpdateRequest
import co.yappuworld.operation.client.dto.response.AdminOperationLinksResponse
import co.yappuworld.operation.client.dto.request.AdminMinSupportVersionUpdateRequest
import co.yappuworld.operation.client.dto.response.AdminMinSupportVersionResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminOperationController(
    private val adminOperationService: AdminOperationService
) : AdminOperationApi {

    override fun getMinSupportVersion(): ResponseEntity<SuccessResponse<AdminMinSupportVersionResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminOperationService.getForceUpdateInfos())
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

    override fun updateOperationLinks(request: AdminOperationLinkUpdateRequest): ResponseEntity<Unit> {
        adminOperationService.updateOperationLink(request)
        return ResponseEntity.noContent().build()
    }
}
