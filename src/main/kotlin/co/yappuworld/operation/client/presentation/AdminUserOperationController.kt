package co.yappuworld.operation.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.client.application.AdminUserOperationService
import co.yappuworld.operation.client.application.ConfigInquiryComponent
import co.yappuworld.operation.client.dto.request.AdminGenerationActiveUpdateRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationDeleteRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationPageRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationRegisterRequest
import co.yappuworld.operation.client.dto.request.AdminSignupCodeDeleteRequest
import co.yappuworld.operation.client.dto.response.AdminGenerationActiveUpdateResponse
import co.yappuworld.operation.client.dto.response.AdminGenerationResponse
import co.yappuworld.user.client.application.AdminUserService
import co.yappuworld.user.client.dto.request.AdminSignUpCodeUpdateRequest
import co.yappuworld.user.client.dto.response.AdminSignUpCodeResponse
import co.yappuworld.user.client.dto.response.AdminSignUpCodesResponse
import co.yappuworld.user.domain.vo.UserRole
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AdminUserOperationController(
    private val adminUserOperationService: AdminUserOperationService,
    private val adminUserService: AdminUserService,
    private val configInquiryComponent: ConfigInquiryComponent
) : AdminUserOperationApi {

    override fun getGenerations(
        request: AdminGenerationPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminGenerationResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(adminUserOperationService.getGenerations(request))
        )

    override fun registerGeneration(request: AdminGenerationRegisterRequest): ResponseEntity<Unit> {
        adminUserOperationService.registerGeneration(request)
        return ResponseEntity.created(URI("/admin/v1/operations")).build()
    }

    override fun deleteGenerations(request: AdminGenerationDeleteRequest): ResponseEntity<SuccessResponse<Unit>> {
        adminUserOperationService.deleteGenerations(request)
        return ResponseEntity.noContent().build()
    }

    override fun updateActiveGeneration(
        request: AdminGenerationActiveUpdateRequest
    ): ResponseEntity<SuccessResponse<AdminGenerationActiveUpdateResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminUserOperationService.updateGenerationActiveStatus(request))
        )

    override fun getSignUpAuthenticationCode(): ResponseEntity<SuccessResponse<AdminSignUpCodesResponse>> {
        val responseByKey = configInquiryComponent
            .findConfigsBy(UserRole.entries.map { it.signUpCodeKey })
            .associateBy { it.id }

        val codes = UserRole.entries.map { role ->
            AdminSignUpCodeResponse(responseByKey[role.signUpCodeKey], role)
        }

        return ResponseEntity.ok(
            SuccessResponse(
                AdminSignUpCodesResponse(codes)
            )
        )
    }

    override fun updateSignUpAuthenticationCode(request: AdminSignUpCodeUpdateRequest): ResponseEntity<Unit> {
        adminUserService.updateSignUpCode(request)
        return ResponseEntity.noContent().build()
    }

    override fun deleteSignUpAuthenticationCode(request: AdminSignupCodeDeleteRequest): ResponseEntity<Unit> {
        adminUserService.deleteSignupCode(request)
        return ResponseEntity.noContent().build()
    }
}
