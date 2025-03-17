package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.application.AdminOperationService
import co.yappuworld.operation.presentation.dto.response.AdminForceUpdateInfoApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminOperationController(
    private val adminOperationService: AdminOperationService
) : AdminOperationApi {

    override fun getForceUpdateInfo(): ResponseEntity<SuccessResponse<AdminForceUpdateInfoApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                AdminForceUpdateInfoApiResponseDto(adminOperationService.getForceUpdateInfos())
            )
        )
}
