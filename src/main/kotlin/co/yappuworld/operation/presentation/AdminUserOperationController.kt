package co.yappuworld.operation.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.application.AdminUserOperationService
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.operation.presentation.dto.request.AdminGenerationActiveUpdateApiRequestDto
import co.yappuworld.operation.presentation.dto.request.AdminGenerationPageApiRequestDto
import co.yappuworld.operation.presentation.dto.request.AdminGenerationRegisterApiRequestDto
import co.yappuworld.operation.presentation.dto.response.AdminGenerationActiveUpdateApiResponseDto
import co.yappuworld.operation.presentation.dto.response.AdminGenerationApiResponseDto
import co.yappuworld.user.application.UserAdminService
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.presentation.dto.request.AdminSignUpCodeUpdateApiRequestDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpCodeApiResponseDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpCodesApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AdminUserOperationController(
    private val adminUserOperationService: AdminUserOperationService,
    private val userAdminService: UserAdminService,
    private val configInquiryComponent: ConfigInquiryComponent
) : AdminUserOperationApi {

    override fun getGenerations(
        request: AdminGenerationPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminGenerationApiResponseDto>>> {
        val response = adminUserOperationService.getGenerations(request.toAppRequest())
        return ResponseEntity.ok(
            SuccessResponse(
                OffsetPageResponse(
                    data = response.generations.map { AdminGenerationApiResponseDto(it) },
                    totalCount = response.totalElements,
                    totalPages = response.totalPages,
                    page = request.page,
                    size = request.size
                )
            )
        )
    }

    override fun registerGeneration(request: AdminGenerationRegisterApiRequestDto): ResponseEntity<Unit> {
        adminUserOperationService.registerGeneration(request.toAppRequest())
        return ResponseEntity.created(URI("/admin/v1/operations")).build()
    }

    override fun updateActiveGeneration(
        request: AdminGenerationActiveUpdateApiRequestDto
    ): ResponseEntity<SuccessResponse<AdminGenerationActiveUpdateApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                AdminGenerationActiveUpdateApiResponseDto(
                    adminUserOperationService.updateGenerationActiveStatus(request.toAppRequest())
                )
            )
        )

    override fun getSignUpAuthenticationCode(): ResponseEntity<SuccessResponse<AdminSignUpCodesApiResponseDto>> {
        val responseByKey = configInquiryComponent
            .findConfigsBy(UserRole.entries.map { it.signUpCodeKey })
            .associateBy { it.id }

        val codes = UserRole.entries.map { role ->
            AdminSignUpCodeApiResponseDto(responseByKey[role.signUpCodeKey], role)
        }

        return ResponseEntity.ok(
            SuccessResponse(
                AdminSignUpCodesApiResponseDto(codes)
            )
        )
    }

    override fun updateSignUpAuthenticationCode(request: AdminSignUpCodeUpdateApiRequestDto): ResponseEntity<Unit> {
        userAdminService.updateSignUpCode(request.toAppRequest())
        return ResponseEntity.noContent().build()
    }
}
