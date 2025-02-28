package co.yappuworld.user.presentation

import co.yappuworld.user.application.SignUpService
import co.yappuworld.user.application.UserAdminService
import co.yappuworld.user.presentation.dto.request.SignUpApplicationApproveApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationRejectApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserRoleUpdateApiRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAuthAdminController(
    private val userAdminService: UserAdminService,
    private val signUpService: SignUpService
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
}
