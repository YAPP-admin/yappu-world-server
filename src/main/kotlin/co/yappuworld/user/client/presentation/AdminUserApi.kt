package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.dto.response.AdminUserProfileResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "어드민 회원 API", description = "_")
interface AdminUserApi {

    @GetMapping("/admin/v1/users/profile")
    fun getUserProfile(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AdminUserProfileResponse>>
}
