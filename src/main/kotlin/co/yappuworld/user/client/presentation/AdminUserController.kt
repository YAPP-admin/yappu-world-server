package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.application.AdminUserService
import co.yappuworld.user.client.dto.response.AdminUserProfileResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminUserController(
    private val adminUserService: AdminUserService
) : AdminUserApi {

    override fun getUserProfile(securityUser: SecurityUser): ResponseEntity<SuccessResponse<AdminUserProfileResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminUserService.getUserProfile(securityUser.userId))
        )
}
