package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.application.UserProfileService
import co.yappuworld.user.presentation.dto.response.UserProfileApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserProfileController(
    private val userProfileService: UserProfileService
) : UserProfileApi {

    override fun getProfile(securityUser: SecurityUser): ResponseEntity<SuccessResponse<UserProfileApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                UserProfileApiResponseDto.of(
                    userProfileService.findUserProfile(securityUser.userId)
                )
            )
        )
}
