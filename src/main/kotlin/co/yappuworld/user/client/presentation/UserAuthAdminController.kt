package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.Token
import co.yappuworld.global.util.TimeUtils.getCurrentDateTimeInKST
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.client.dto.request.SignUpApplicationApproveRequest
import co.yappuworld.user.client.dto.request.SignUpApplicationRejectRequest
import co.yappuworld.user.client.dto.response.AdminSignUpApplicationOverviewResponse
import co.yappuworld.user.client.application.SignUpService
import co.yappuworld.user.client.application.UserAdminService
import co.yappuworld.user.client.dto.request.AdminSignUpApplicationPageRequest
import co.yappuworld.user.client.dto.request.UserRoleUpdateRequest
import co.yappuworld.user.client.dto.response.AdminSignUpApplicationResponse
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
            SuccessResponse(userAdminService.login(request, getCurrentDateTimeInKST()))
        )

    override fun updateUserRole(request: UserRoleUpdateRequest): ResponseEntity<Unit> {
        userAdminService.updateUserRole(request)
        return ResponseEntity.noContent().build()
    }

    override fun approveSignUpApplication(request: SignUpApplicationApproveRequest): ResponseEntity<Unit> {
        signUpService.approveSignUpApplication(request)
        return ResponseEntity.noContent().build()
    }

    override fun rejectSignUpApplication(request: SignUpApplicationRejectRequest): ResponseEntity<Unit> {
        signUpService.rejectSignUpApplication(request)
        return ResponseEntity.noContent().build()
    }

    override fun getSignUpApplications(
        request: AdminSignUpApplicationPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSignUpApplicationOverviewResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(userAdminService.getSignUpApplications(request))
        )

    override fun getSignUpApplication(
        applicationId: UUID
    ): ResponseEntity<SuccessResponse<AdminSignUpApplicationResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userAdminService.getSignUpApplicationDetails(applicationId))
        )
}
