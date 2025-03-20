package co.yappuworld.user.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.Token
import co.yappuworld.global.util.TimeUtils.getCurrentDateTimeInKST
import co.yappuworld.user.application.SignUpService
import co.yappuworld.user.application.UserAdminService
import co.yappuworld.user.application.dto.request.LoginRequest
import co.yappuworld.user.presentation.dto.request.AdminSignUpApplicationPageApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationApproveApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationRejectApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserRoleUpdateApiRequestDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpApplicationApiResponseDto
import co.yappuworld.user.application.dto.response.AdminSignUpApplicationOverviewResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserAuthAdminController(
    private val userAdminService: UserAdminService,
    private val signUpService: SignUpService
) : UserAuthAdminApi {

    override fun login(request: LoginRequest): ResponseEntity<SuccessResponse<Token>> =
        ResponseEntity.ok(
            SuccessResponse(
                userAdminService.login(request, getCurrentDateTimeInKST())
            )
        )

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
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSignUpApplicationOverviewResponse>>> =
        userAdminService.getSignUpApplications(request.toAppRequest()).let {
            ResponseEntity.ok(
                SuccessResponse(it)
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
}
