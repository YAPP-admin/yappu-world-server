package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.application.UserAuthAdminService
import co.yappuworld.user.presentation.dto.request.SignUpApplicationApproveApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationRejectApiRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAuthAdminController(
    private val userAuthAdminService: UserAuthAdminService
) : UserAuthAdminApi {
    override fun approveSignUpApplication(
        request: SignUpApplicationApproveApiRequestDto
    ): ResponseEntity<SuccessResponse<Unit>> {
        userAuthAdminService.approveSignUpApplication(request.toAppRequest())
        return ResponseEntity.ok(SuccessResponse())
    }

    override fun rejectSignUpApplication(
        request: SignUpApplicationRejectApiRequestDto
    ): ResponseEntity<SuccessResponse<Unit>> {
        userAuthAdminService.rejectSignUpApplication(request.toAppRequest())
        return ResponseEntity.ok(SuccessResponse())
    }
}
