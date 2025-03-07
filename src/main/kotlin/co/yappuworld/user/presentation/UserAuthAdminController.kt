package co.yappuworld.user.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.application.ConfigInquiryComponent
import co.yappuworld.user.application.SignUpService
import co.yappuworld.user.application.UserAdminService
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.presentation.dto.request.AdminSignUpApplicationPageApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationApproveApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationRejectApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserRoleUpdateApiRequestDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpApplicationApiResponseDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpApplicationOverviewApiResponseDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpAuthenticationCodeApiResponseDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpAuthenticationCodesApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserAuthAdminController(
    private val userAdminService: UserAdminService,
    private val signUpService: SignUpService,
    private val configInquiryComponent: ConfigInquiryComponent
) : UserAuthAdminApi {

    override fun updateUserRole(request: UserRoleUpdateApiRequestDto): ResponseEntity<Unit> {
        userAdminService.updateUserRole(request.toAppRequest())
        return ResponseEntity.noContent().build()
    }

    override fun approveSignUpApplication(request: SignUpApplicationApproveApiRequestDto): ResponseEntity<Unit> {
        signUpService.approveSignUpApplication(request.toAppRequest())
        return ResponseEntity.noContent().build()
    }

    override fun rejectSignUpApplication(request: SignUpApplicationRejectApiRequestDto): ResponseEntity<Unit> {
        signUpService.rejectSignUpApplication(request.toAppRequest())
        return ResponseEntity.noContent().build()
    }

    override fun getSignUpApplications(
        request: AdminSignUpApplicationPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSignUpApplicationOverviewApiResponseDto>>> =
        userAdminService.getSignUpApplications(request.toAppRequest()).let {
            ResponseEntity.ok(
                SuccessResponse(
                    OffsetPageResponse(
                        data = it.data.map { d -> AdminSignUpApplicationOverviewApiResponseDto(d) },
                        totalCount = it.totalCount,
                        page = request.page,
                        size = request.size
                    )
                )
            )
        }

    override fun getSignUpApplication(
        applicationId: UUID
    ): ResponseEntity<SuccessResponse<AdminSignUpApplicationApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                AdminSignUpApplicationApiResponseDto(
                    userAdminService.getSignUpApplicationDetails(applicationId)
                )
            )
        )

    override fun getSignUpAuthenticationCode():
        ResponseEntity<SuccessResponse<AdminSignUpAuthenticationCodesApiResponseDto>> {
        val responseByKey = configInquiryComponent
            .findConfigsBy(UserRole.entries.map { it.authenticationCodeKey })
            .associateBy { it.id }

        val codes = UserRole.entries.map { role ->
            AdminSignUpAuthenticationCodeApiResponseDto(responseByKey[role.authenticationCodeKey], role)
        }

        return ResponseEntity.ok(
            SuccessResponse(
                AdminSignUpAuthenticationCodesApiResponseDto(codes)
            )
        )
    }
}
